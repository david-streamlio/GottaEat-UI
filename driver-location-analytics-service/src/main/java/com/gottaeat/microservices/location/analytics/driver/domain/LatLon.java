package com.gottaeat.microservices.location.analytics.driver.domain;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LatLon {

    @NotNull
    public float latitude;

    @NotNull
    public float longitude;

    public LatLon() {
    }

    /**
     * For analytics purposes, we truncate the floats at 1 decimal places of precision.
     */
    public LatLon(float lat, float lon) {
        BigDecimal a = new BigDecimal(lat);
        BigDecimal roundOff = a.setScale(1, RoundingMode.HALF_UP);
        this.latitude = roundOff.floatValue();

        a = new BigDecimal(lon);
        roundOff = a.setScale(1, RoundingMode.HALF_UP);
        this.longitude = roundOff.floatValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LatLon)) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            LatLon other = (LatLon)o;
            return (other.latitude == this.latitude) && (other.longitude == this.longitude);
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Float.valueOf(latitude).hashCode();
        result = 31 * result + Float.valueOf(longitude).hashCode();
        return result;
    }
}
