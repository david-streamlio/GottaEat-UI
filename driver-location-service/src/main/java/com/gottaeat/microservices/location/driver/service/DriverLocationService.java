package com.gottaeat.microservices.location.driver.service;

import com.gottaeat.microservices.common.beans.GridLookupBean;
import com.gottaeat.microservices.common.beans.PulsarBean;
import com.gottaeat.microservices.location.driver.domain.DriverLocation;
import com.gottaeat.microservices.location.driver.domain.DriverPositionSignal;
import org.apache.pulsar.client.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DriverLocationService {

    private final Logger LOGGER = LoggerFactory.getLogger(DriverLocationService.class);

    @Inject
    PulsarBean pulsar;

    @Inject
    GridLookupBean lookup;

    @Inject
    @ConfigProperty(name = "dls.topic", defaultValue = "persistent://public/default/driverLocation")
    String topic;

    private Producer<DriverLocation> producer;

    public void record(DriverPositionSignal cmd) {

        try {
            DriverLocation enriched = new DriverLocation(cmd);
            enriched.gridId = lookup.getGridIdByLatLon(cmd.latitude, cmd.longitude);

            getProducer().newMessage()
                    .eventTime(cmd.timestamp)
                    .key(Long.toString(cmd.driverId))
                    .value(enriched)
                    .send();

        } catch (final Exception ex) {
            LOGGER.error("Unable to publish to Pulsar", ex);
        }
    }

    private Producer<DriverLocation> getProducer() throws PulsarClientException {
        if (producer == null) {
           producer = this.pulsar.getPulsarClient()
                   .newProducer(Schema.JSON(DriverLocation.class))
                   .topic(this.topic)
                   .create();
        }

        return producer;
    }
}
