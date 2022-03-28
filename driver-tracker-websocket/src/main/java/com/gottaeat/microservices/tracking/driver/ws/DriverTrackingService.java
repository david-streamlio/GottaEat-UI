package com.gottaeat.microservices.tracking.driver.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gottaeat.commons.beans.PulsarBean;
import com.gottaeat.microservices.location.driver.domain.DriverLocation;
import org.apache.pulsar.client.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/tracking/drivers/{driverId}")
@ApplicationScoped
public class DriverTrackingService {

    private static final Logger LOGGER = Logger.getLogger(DriverTrackingService.class);

    @Inject
    PulsarBean pulsar;

    @Inject
    @ConfigProperty(name = "dls.topic", defaultValue = "persistent://public/default/driverLocation")
    String topic;

    private ObjectMapper mapper = new ObjectMapper();

    // Assumes one session per driver.
    Map<String, List<Session>> sessions = new ConcurrentHashMap<>();

    private Consumer<DriverLocation> consumer;

    @OnOpen
    public void onOpen(Session session, @PathParam("driverId") String driverId) {
        if (!sessions.containsKey(driverId)) {
            sessions.put(driverId, new ArrayList<Session>());
        }
        sessions.get(driverId).add(session);

        startConsumer();
    }

    @OnClose
    public void onClose(Session session, @PathParam("driverId") String driverId) {
        if (sessions.containsKey(driverId) && !sessions.get(driverId).isEmpty()) {
            sessions.get(driverId).remove(session);
        }

        // Clean up empty lists
        if (sessions.get(driverId).isEmpty()) {
            sessions.remove(driverId);
        }
    }

    private void notify(String driverId, DriverLocation loc) {
        if (sessions.containsKey(driverId)) {
            sessions.get(driverId).forEach(session -> {
                try {
                    session.getAsyncRemote().sendObject(mapper.writeValueAsString(loc), result -> {
                        if (result.getException() != null) {
                            LOGGER.error("Unable to send message: " + result.getException());
                        }
                    });
                } catch (JsonProcessingException e) {
                    LOGGER.error("Unable to send message:", e);
                }
            });
        }
    }

    private void startConsumer() {
        if (consumer == null) {
            try {
                consumer = this.pulsar.getPulsarClient()
                        .newConsumer(Schema.JSON(DriverLocation.class))
                        .topic(topic)
                        .subscriptionName("tracker")
                        .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                        .subscriptionType(SubscriptionType.Exclusive)
                        .messageListener((con, msg) -> {
                            try {
                                if (sessions.containsKey(msg.getKey())) {
                                    this.notify(msg.getKey(), msg.getValue());
                                }
                                con.acknowledge(msg);
                            } catch (PulsarClientException e) {
                                LOGGER.error("Can't process message", e);
                            }
                        })
                        .subscribe();
            } catch (PulsarClientException e) {
                LOGGER.error("Unable to subscribe", e);
            }
        }
    }

}
