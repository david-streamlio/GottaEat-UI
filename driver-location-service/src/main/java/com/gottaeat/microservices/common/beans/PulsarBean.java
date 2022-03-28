package com.gottaeat.microservices.common.beans;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class PulsarBean {

    private final Logger LOGGER = LoggerFactory.getLogger(PulsarBean.class);

    @Inject
    PulsarClientConfig config;

    private PulsarClient pulsarClient;

    void startup(@Observes StartupEvent event) {
        LOGGER.debug("Starting PulsarBean...");
        try {
            this.pulsarClient = PulsarClient.builder()
                    .serviceUrl(config.serviceUrl())
                    .allowTlsInsecureConnection(config.allowTlsInsecureConnection())
                    .connectionsPerBroker(config.connectionsPerBroker())
                    .enableBusyWait(config.enableBusyWait())
                    .enableTcpNoDelay(config.enableTcpNoDelay())
                    .enableTlsHostnameVerification(config.enableTlsHostnameVerification())
                    .enableTransaction(config.enableTransaction())
                    .build();
        } catch (PulsarClientException pcl) {
            LOGGER.error("Error starting pulsar client", pcl);
        }
    }

    void onStop(@Observes ShutdownEvent ev) throws IOException {
        this.pulsarClient.close();
    }

    public PulsarClient getPulsarClient() {
        return this.pulsarClient;
    }
}
