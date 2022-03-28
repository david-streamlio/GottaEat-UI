package com.gottaeat.microservices.tracking.driver.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DriverLocation {

    @NotNull
    public float latitude;

    @NotNull
    public float longitude;

    @NotNull
    public long timestamp;

    @Min(1)
    public long driverId;

    public String gridId;

    public DriverLocation() {

    }
}
