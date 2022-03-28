package com.gottaeat.microservices.beans;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.concurrent.TimeUnit;

@ConfigMapping(prefix = "cache")
public interface CacheConfig {

    @WithDefault("1000")
    int maximumSize();

    @WithDefault("10")
    int expireAfterWriteDuration();

}
