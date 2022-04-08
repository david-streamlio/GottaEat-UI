import React, {forwardRef, useEffect, useRef, useState} from 'react';
import axios from 'axios'
import Locations from "./locations";

const DriverLocations = () => {

    const [ locations, setLocations ] = useState( [] );

    const fetchLocationData = async () => {
        const response = await axios.get('/location/driver/currentLocations');
        setLocations(response.data);
    }

    useEffect(() => {
        fetchLocationData(); // Load the grid data from the REST endpoint

        const id = setInterval(() => {  // Schedule the data to be refreshed
            fetchLocationData();
        }, 10 * 1000)

        return () => clearInterval(id)
    }, []) // Set dependencies to empty array to prevent infinite loop

    return (<div>
        <Locations locations={locations} />
    </div>);
}

export default DriverLocations