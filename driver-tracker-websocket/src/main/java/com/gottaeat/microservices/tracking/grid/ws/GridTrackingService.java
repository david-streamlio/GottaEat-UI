package com.gottaeat.microservices.tracking.grid.ws;

import com.gottaeat.microservices.location.driver.domain.DriverLocation;
import com.gottaeat.microservices.tracking.AbstractTrackingService;

import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/tracking/grid/{gridId}")
@ApplicationScoped
public class GridTrackingService extends AbstractTrackingService {

    private static final Logger LOGGER = Logger.getLogger(GridTrackingService.class);

    @Inject
    @ConfigProperty(name = "grid-tracking-service.subscription-name", defaultValue = "grid-tracking")
    String subscriptionName;

    @OnOpen
    public void onOpen(Session session, @PathParam("gridId") String gridId) {
        super.handleOnOpen(session, gridId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("gridId") String gridId) {
        super.handleOnClose(session, gridId);
    }

    @Override
    protected String getSubscriptionName() {
        return subscriptionName;
    }

    /**
     * In this case we use the gridId field of message itself to
     * notify all the subscribers on the websocket.
     *
     * SELECT * from dls.topic where gridId = ?
     */
    @Override
    protected MessageListener<DriverLocation> getMessageListener() {
        MessageListener<DriverLocation> listener = (consumer, msg) -> {
            try {
                String key = msg.getValue().gridId;
                if (sessions.containsKey(key)) {
                    notify(key, msg.getValue());
                }
                consumer.acknowledge(msg);
            } catch (PulsarClientException e) {
                LOGGER.error("Unable to ack message", e);
            }
        };

        return listener;
    }
}
