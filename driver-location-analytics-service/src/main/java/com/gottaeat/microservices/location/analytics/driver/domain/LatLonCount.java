package com.gottaeat.microservices.location.analytics.driver.domain;

public class LatLonCount {

    public float lat;

    public float lon;

    public int count;

    public LatLonCount() { }

    public LatLonCount(float lat, float lon, int count) {
        this.lat = lat;
        this.lon = lon;
        this.count = count;
    }
}
