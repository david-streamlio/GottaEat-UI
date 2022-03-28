package com.gottaeat.microservices.beans;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

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

}
