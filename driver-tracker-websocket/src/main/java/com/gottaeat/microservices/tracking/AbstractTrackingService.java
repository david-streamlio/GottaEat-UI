package com.gottaeat.microservices.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gottaeat.commons.beans.PulsarBean;
import com.gottaeat.microservices.location.driver.domain.DriverLocation;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTrackingService {

    private static final Logger LOGGER = Logger.getLogger(AbstractTrackingService.class);

    @Inject
    PulsarBean pulsar;

    @Inject
    @ConfigProperty(name = "dls.topic", defaultValue = "persistent://public/default/driverLocation")
    String topic;

    protected ObjectMapper mapper = new ObjectMapper();

    // Supports multiple sessions per id.
    protected Map<String, List<Session>> sessions = new ConcurrentHashMap<>();

    protected Consumer<DriverLocation> consumer;

    protected abstract String getSubscriptionName();

    protected abstract MessageListener<DriverLocation> getMessageListener();

    void startup(@Observes StartupEvent event) {
        startConsumer();
    }

    protected void handleOnOpen(Session session, String key) {
        if (!sessions.containsKey(key)) {
            sessions.put(key, new ArrayList<Session>());
        }
        sessions.get(key).add(session);

        startConsumer(); // Just in case the consumer hasn't been started.
    }

    protected void handleOnClose(Session session, String key) {
        if (sessions.containsKey(key) && !sessions.get(key).isEmpty()) {
            sessions.get(key).remove(session);
        }

        // Clean up empty lists
        if (sessions.get(key).isEmpty()) {
            sessions.remove(key);
        }
    }

    protected void notify(String key, DriverLocation loc) {
        if (sessions.containsKey(key)) {
            sessions.get(key).forEach(session -> {
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

    protected void startConsumer() {
        if (consumer == null) {
            try {
                consumer = this.pulsar.getPulsarClient()
                        .newConsumer(Schema.JSON(DriverLocation.class))
                        .topic(topic)
                        .subscriptionName(getSubscriptionName())
                        .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                        .subscriptionType(SubscriptionType.Exclusive)
                        .messageListener(getMessageListener())
                        .subscribe();
            } catch (PulsarClientException e) {
                LOGGER.error("Unable to subscribe", e);
            }
        }
    }

}
