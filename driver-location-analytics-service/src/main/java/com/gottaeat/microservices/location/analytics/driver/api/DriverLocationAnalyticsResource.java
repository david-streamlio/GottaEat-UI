package com.gottaeat.microservices.location.analytics.driver.api;

import com.gottaeat.microservices.location.analytics.driver.domain.LatLonCount;
import com.gottaeat.microservices.location.analytics.driver.respository.DriverLocationAnalyticsRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/analytics/driver/location")
public class DriverLocationAnalyticsResource {

    @Inject
    DriverLocationAnalyticsRepository repository;

    @GET
    public List<LatLonCount> getHeatMap() {
        return repository.getHeatMap();
    }

    @GET
    @Path("/{gridId}")
    public List<LatLonCount> getHeatMapByGridId(@PathParam("gridId") String gridId) {
        return repository.getHeatMapByGridId(gridId);
    }
}
