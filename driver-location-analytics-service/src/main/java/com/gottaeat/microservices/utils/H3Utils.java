package com.gottaeat.microservices.utils;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import org.geojson.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class H3Utils {

    private static H3Core h3;

    static {
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Convert a set of hexagons to a GeoJSON `FeatureCollection` with each hexagon
     * in a separate `Polygon` feature with optional properties.
     *
     * ![h3SetToFeatureCollection](./doc-files/h3SetToFeatureCollection.png)
     * @static
     * @param  {String[]} hexagons  Hexagon addresses
     *                                    for a hexagon: f(h3Index) => Object
     * @return {FeatureCollection}        GeoJSON FeatureCollection object
     */
    public static FeatureCollection  h3SetToFeatureCollection(String... hexagons) {
        FeatureCollection collection = new FeatureCollection();

        for (String hexagon : hexagons) {
            collection.add(h3ToFeature(hexagon));
        }

        return collection;
    }

    /**
     * Convert a set of hexagons to a GeoJSON `Feature` with the set outline(s). The
     * feature's geometry type will be either `Polygon` or `MultiPolygon` depending on
     * the number of outlines required for the set.
     *
     * @param  {String[]} hexagons   Hexagon addresses
     * @param  {Object} [properties] Optional feature properties
     * @return {Feature}             GeoJSON Feature object
     */
    private static Feature h3ToFeature(String hexagon) {
        Feature feature = new Feature();
        List<List<List<GeoCoord>>> polygons = h3.h3AddressSetToMultiPolygon(Collections.singleton(hexagon), true);

        if (polygons.size() > 1) {
            MultiPolygon m = buildMultiPolygon(polygons);
            feature.setGeometry(m);
        } else if (!polygons.isEmpty()) {
            feature.setGeometry(buildPolygon(polygons.get(0)));
        }

        return feature;
    }

    private static MultiPolygon buildMultiPolygon(List<List<List<GeoCoord>>> polygons) {
        MultiPolygon m = new MultiPolygon();
        List<List<List<LngLatAlt>>> coordinates = new ArrayList<List<List<LngLatAlt>>>();

        polygons.forEach(polygon -> {
            List<List<LngLatAlt>> innerList = new ArrayList<List<LngLatAlt>>();
            polygon.forEach(geoCoordList -> {
                List<LngLatAlt> innerMostList = new ArrayList<LngLatAlt>();
                geoCoordList.forEach(coordinate -> {
                    LngLatAlt ll = new LngLatAlt();
                    ll.setLatitude(coordinate.lat);
                    ll.setLongitude(coordinate.lng);
                    innerMostList.add(ll);
                });
                innerList.add(innerMostList);
            });
            coordinates.add(innerList);
        });

        return m;
    }

    private static Polygon buildPolygon(List<List<GeoCoord>> lists) {
        Polygon p = new Polygon();
        List<List<LngLatAlt>> coordinates = new ArrayList<List<LngLatAlt>>();

        lists.forEach(geoCoords -> {
            List<LngLatAlt> innerList = new ArrayList<LngLatAlt>();
            geoCoords.forEach(coordinate -> {
                LngLatAlt ll = new LngLatAlt();
                ll.setLatitude(coordinate.lat);
                ll.setLongitude(coordinate.lng);
                innerList.add(ll);
            });
            coordinates.add(innerList);
        });

        p.setCoordinates(coordinates);
        return p;
    }
}
