package com.gottaeat.microservices.location.analytics.driver.respository;

import com.gottaeat.microservices.beans.CacheBean;
import com.gottaeat.microservices.beans.PulsarBean;
import com.gottaeat.microservices.location.analytics.driver.domain.DriverLocation;
import com.gottaeat.microservices.location.analytics.driver.domain.LatLon;
import com.gottaeat.microservices.location.analytics.driver.domain.LatLonCount;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reconstruct every 5 minutes or expire old position data either periodically
 * or if a driver "moves".
 */
@ApplicationScoped
public class DriverLocationAnalyticsRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(DriverLocationAnalyticsRepository.class);

    @Inject
    PulsarBean pulsar;

    /**
     * We keep a cache of <DriverId, LatLon> to track each driver's
     * last reported location.
     */
    @Inject
    CacheBean cache;

    @Inject
    @ConfigProperty(name = "dl.topic", defaultValue = "persistent://public/default/driverLocation")
    String topic;

    private Reader<DriverLocation> reader;
    private Consumer<DriverLocation> consumer;

    public List<LatLonCount> getHeatMap() {
        return report();
    }

    public List<LatLonCount> getHeatMapByGridId(String gridId) {
        return null;
    }

    /**
     * When we first start up, we need to populate the cache with historical
     * data using the Reader interface. Then we launch an active consumer on
     * the topic to read in real-time data.
     *
     * @param event
     * @throws PulsarClientException
     */
    void startup(@Observes StartupEvent event) {
        LOGGER.debug("Starting ...");

        try {
            // Populate with historical data
            while (getReader().hasMessageAvailable()) {
                Message<DriverLocation> msg = getReader().readNext();
                cache.getCache().put(msg.getValue().driverId,
                        new LatLon(msg.getValue().latitude, msg.getValue().longitude));
            }

            // Start the consumer
            getConsumer();
        } catch (PulsarClientException e) {
            LOGGER.error("Failed to initialize....", e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) throws IOException {
        getReader().close();
        getConsumer().unsubscribe();
        getConsumer().close();
    }

    /**
     * Helper method to convert the cache data into the desired format.
     */
    private List<LatLonCount> report() {
        List<LatLonCount> report = new ArrayList<LatLonCount>();

        Map<LatLon, Integer> sum = new HashMap<LatLon, Integer>();
        cache.getCache().asMap().forEach( (driverId, loc) -> {
            LOGGER.info("Processing Location " + loc.latitude + " " + loc.longitude);
            if (!sum.containsKey(loc)) {
                sum.put(loc, 1);
            } else {
                sum.put(loc, sum.get(loc) + 1);
            }
        });

        sum.keySet().forEach(key -> {
            report.add(new LatLonCount(key.latitude, key.longitude, sum.get(key)));
        });

        return report;
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

    /**
     * Start a consumer on the topic in order to keep the cache data up to
     * date based on the incoming location events.
     *
     * @return
     * @throws PulsarClientException
     */
    private Consumer<DriverLocation> getConsumer() throws PulsarClientException {
        if (consumer == null) {
            consumer = this.pulsar.getPulsarClient()
                    .newConsumer(Schema.JSON(DriverLocation.class))
                    .topic(topic)
                    .subscriptionName("analytics")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                    .subscriptionType(SubscriptionType.Exclusive)
                    .messageListener( (con, msg) -> {
                        try {
                            cache.getCache().put(msg.getValue().driverId,
                                    new LatLon(msg.getValue().latitude, msg.getValue().longitude));
                            con.acknowledge(msg);
                        } catch (PulsarClientException e) {
                            LOGGER.error("Can't process message", e);
                        }
                    })
                    .subscribe();
        }

        return consumer;
    }
}
