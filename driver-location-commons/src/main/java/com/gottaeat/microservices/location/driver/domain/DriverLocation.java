package com.gottaeat.microservices.location.driver.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DriverLocation implements Comparable<DriverLocation> {

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

    public DriverLocation(DriverPositionSignal signal) {
        this.latitude = signal.latitude;
        this.longitude = signal.longitude;
        this.driverId = signal.driverId;
        this.timestamp = signal.timestamp;
    }

    @Override
    public int compareTo(DriverLocation o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
