package com.gottaeat.microservices.location.driver.repository;

import com.gottaeat.commons.beans.PulsarBean;
import com.gottaeat.microservices.location.driver.domain.DriverLocation;
import io.quarkus.runtime.ShutdownEvent;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;
import org.apache.pulsar.client.api.Schema;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DriverLocationRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(DriverLocationRepository.class);

    @Inject
    PulsarBean pulsar;

    @Inject
    @ConfigProperty(name = "dls.topic", defaultValue = "persistent://public/default/driverLocation")
    String topic;

    private Reader<DriverLocation> reader;

    void onStop(@Observes ShutdownEvent ev) throws IOException {
        if (reader != null) {
            getReader().close();
        }
    }

    public List<DriverLocation> list() {
        List<DriverLocation> locations = new ArrayList<>();
        try {
            getReader().seek(MessageId.earliest);
            while (getReader().hasMessageAvailable()) {
                locations.add(reader.readNext().getValue());
            }

        } catch (PulsarClientException e) {
            LOGGER.error("Unable to connect to Pulsar", e);
        }
        return locations;
    }

    public List<DriverLocation> getByDriverId(long driverId) {
        return list().stream()
                .filter(d -> d.driverId == driverId)
                .collect(Collectors.toList());
    }

    private Reader<DriverLocation> getReader() throws PulsarClientException {
        if (reader == null) {
            reader = this.pulsar.getPulsarClient()
                    .newReader(Schema.JSON(DriverLocation.class))
                    .topic(topic)
                    .startMessageId(MessageId.earliest)
                    .create();
        }
        return reader;
    }
}
