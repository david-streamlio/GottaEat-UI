package com.gottaeat.commons.beans;

import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

@ApplicationScoped
public class PulsarBean {

    private final Logger LOGGER = LoggerFactory.getLogger(PulsarBean.class);

    @Inject
    PulsarClientConfig config;

    private PulsarClient pulsarClient;

    void startup(@Observes StartupEvent event) {
        LOGGER.debug("Starting PulsarBean...");
        createPulsarClient();
    }

    public PulsarClient getPulsarClient() {
        if (this.pulsarClient == null) {
            createPulsarClient();
        }
        return this.pulsarClient;
    }

    private void createPulsarClient() {
        try {
            this.pulsarClient = PulsarClient.builder()
                    .serviceUrl(config.serviceUrl())
                    .allowTlsInsecureConnection(config.allowTlsInsecureConnection())
                    .authentication(getAuthentication())
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

    private Authentication getAuthentication() {
        Authentication auth = null;

        if (config.oauth2().isPresent()) {
            PulsarClientConfig.OAuth2 authConfig = config.oauth2().get();
            try {
                URL issuerUrl = new URL(authConfig.issuerUrl().get());
                URL credentialsUrl = new URL(authConfig.credentialsUrl().get());
                auth = AuthenticationFactoryOAuth2.clientCredentials(issuerUrl,
                        credentialsUrl, authConfig.audience().get());

            } catch(MalformedURLException e) {
                LOGGER.error("Bad security credentials provided", e);
            }
        } // else if ... other authentication type, etc.
        return auth;
    }

}
