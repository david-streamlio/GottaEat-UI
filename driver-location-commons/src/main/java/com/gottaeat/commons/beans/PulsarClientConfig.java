package com.gottaeat.commons.beans;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

@ConfigMapping(prefix = "pulsar")
public interface PulsarClientConfig {

    @WithDefault("false")
    boolean allowTlsInsecureConnection();

    @WithDefault("1")
    int connectionsPerBroker();

    @WithDefault("false")
    boolean enableBusyWait();

    @WithDefault("false")
    boolean enableTcpNoDelay();

    @WithDefault("false")
    boolean enableTlsHostnameVerification();

    @WithDefault("false")
    boolean enableTransaction();

    String serviceUrl();

    Optional<OAuth2> oauth2();

    Optional<Jwt> jwt();

    interface OAuth2 {
        String issuerUrl();
        String credentialsUrl();
        String audience();
    }

    interface Jwt {
        String token();
    }
}

