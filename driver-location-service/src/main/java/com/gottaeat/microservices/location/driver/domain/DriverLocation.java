package com.gottaeat.microservices.location.driver.domain;

public class DriverLocation extends DriverPositionSignal {

    public String gridId;

    public DriverLocation() { }

    public DriverLocation(DriverPositionSignal signal) {
        this.driverId = signal.driverId;
        this.latitude = signal.latitude;
        this.longitude = signal.longitude;
        this.timestamp = signal.timestamp;
    }
}
