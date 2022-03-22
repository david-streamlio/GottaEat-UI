package com.gottaeat.microservices.common.beans;

import com.uber.h3core.H3Core;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;

@ApplicationScoped
public class GridLookupBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridLookupBean.class);

    @ConfigProperty(name = "h3.resolution", defaultValue = "9")
    int resolution;

    private H3Core h3;

    public String getGridIdByLatLon(float lat, float lon) {
        return h3.geoToH3Address(lat, lon, resolution);
    }

    void startup(@Observes StartupEvent event) {
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            LOGGER.error("Unable to create H3", e);
        }
    }
}
