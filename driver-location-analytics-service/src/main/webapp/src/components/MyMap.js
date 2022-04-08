import React, {forwardRef, useEffect, useRef, useState} from 'react';
import {MapContainer, GeoJSON, TileLayer, useMapEvents, useMapEvent} from "react-leaflet";
import axios from 'axios'
import "leaflet/dist/leaflet.css";
import "./MyMap.css";

const MyMap = () => {
    const mapRef = useRef(null);
    const [ mapData, setMapData ] = useState( );
    const [ mapCenter, setMapCenter ] = useState([41.84069, -88.022035]);
    const [ mapZoom, setMapZoom ] = useState( 12 );

    const fetchMapData = async () => {
        const response = await axios.get('/analytics/driver/location/geojson');
        setMapData(response.data);
    }

    useEffect(() => {
        fetchMapData(mapData); // Load the grid data from the REST endpoint

        const id = setInterval(() => {  // Schedule the data to be refreshed
            fetchMapData(mapData);
        }, 60 * 1000)

        return () => clearInterval(id)
    }, []) // Set dependencies to empty array to prevent infinite loop

    const gridStyle = {
        fillColor: "red",
        fillOpacity: 1,
        color: "black",
        weight: 2
    }

    const onEachGrid = (country, layer) => {
        layer.options.fillOpacity = Math.random();
    }

    function MapStateChangeTracker() {
        const map = useMapEvents({
            zoomend: () => {
                const z = map.getZoom();
                console.log('Zoom End', z)
                setMapZoom(z);
            },
            moveend: () => {
                const c = map.getCenter();
                console.log('move end', c);
                setMapCenter(c);
            }
        })
        return null
    }

    const Map = forwardRef((props, ref) => {
        return <MapContainer style={{height: "80vh"}} zoom={mapZoom} center={mapCenter} ref={ref}>
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <GeoJSON style={gridStyle} data={mapData.features} onEachFeature={(onEachGrid)}/>
            <MapStateChangeTracker/>
        </MapContainer>
    });

    /* Use the technique described here, https://daveceddia.com/react-before-render/
     * to block the rendering until after the feature data has loaded.
     */
    return (<div>{mapData &&
        <Map ref={mapRef}/>}
    </div>);
}

export default MyMap