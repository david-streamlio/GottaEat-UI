package com.gottaeat.microservices.tracking.driver.ws;

import com.gottaeat.microservices.location.driver.domain.DriverLocation;
import com.gottaeat.microservices.tracking.AbstractTrackingService;
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


@ServerEndpoint("/tracking/drivers/{driverId}")
@ApplicationScoped
public class DriverTrackingService extends AbstractTrackingService {

    private static final Logger LOGGER = Logger.getLogger(DriverTrackingService.class);

    @Inject
    @ConfigProperty(name = "driver-tracking-service.subscription-name", defaultValue = "driver-tracking")
    String subscriptionName;

    @OnOpen
    public void onOpen(Session session, @PathParam("driverId") String driverId) {
       super.handleOnOpen(session, driverId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("driverId") String driverId) {
       super.handleOnClose(session, driverId);
    }

    @Override
    protected String getSubscriptionName() {
        return subscriptionName;
    }

    /**
     * In this case we use the message key, which we know is the driver id, to
     * notify all the subscribers on the websocket.
     *
     * SELECT * from dls.topic where driverId = ?
     */
    @Override
    protected MessageListener<DriverLocation> getMessageListener() {
        MessageListener<DriverLocation> listener = (consumer, msg) -> {
            try {
                if (sessions.containsKey(msg.getKey())) {
                    notify(msg.getKey(), msg.getValue());
                }
                consumer.acknowledge(msg);
            } catch (PulsarClientException e) {
                LOGGER.error("Unable to ack message", e);
            }
        };

        return listener;
    }
}
