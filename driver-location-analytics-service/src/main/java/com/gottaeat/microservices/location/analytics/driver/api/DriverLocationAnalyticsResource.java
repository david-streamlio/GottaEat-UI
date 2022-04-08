package com.gottaeat.microservices.location.analytics.driver.api;

import com.gottaeat.microservices.location.analytics.driver.domain.LatLonCount;
import com.gottaeat.microservices.location.analytics.driver.respository.DriverLocationAnalyticsRepository;
import com.gottaeat.microservices.utils.H3Utils;

import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;


@Path("/analytics/driver/location")
public class DriverLocationAnalyticsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverLocationAnalyticsResource.class);

    @Inject
    DriverLocationAnalyticsRepository repository;

    @GET
    @Path("/geojson")
    @Produces({MediaType.APPLICATION_JSON})
    public FeatureCollection getGeo() throws IOException {
        return H3Utils.h3SetToFeatureCollection(repository.getGrids().toArray(String[]::new));
    }

    @GET
    @Path("/geojson/{gridId}")
    @Produces({MediaType.APPLICATION_JSON})
    public FeatureCollection getHeatMapByGridId(@PathParam("gridId") String gridId) {
        return H3Utils.h3SetToFeatureCollection(new String[]{gridId});
    }
}
