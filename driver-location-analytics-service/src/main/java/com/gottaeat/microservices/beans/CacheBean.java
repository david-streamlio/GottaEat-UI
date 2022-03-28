package com.gottaeat.microservices.beans;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gottaeat.microservices.location.analytics.driver.domain.DriverLocation;
import com.gottaeat.microservices.location.analytics.driver.domain.LatLon;
import com.gottaeat.microservices.location.analytics.driver.domain.LatLonCount;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
public class CacheBean {

    @Inject
    CacheConfig config;

    private Cache<Long, LatLon> cache;

    public Cache<Long, LatLon> getCache() {
        return this.cache;
    }

    void startup(@Observes StartupEvent event) {
        createCache();
    }

    private void createCache() {
        if (this.cache == null) {
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(config.expireAfterWriteDuration(), TimeUnit.MINUTES)
                    .maximumSize(config.maximumSize())
                    .build();
        }
    }
}
