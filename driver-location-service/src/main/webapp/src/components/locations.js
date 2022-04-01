import React from 'react'

const Locations = ({ locations }) => {
    return (
        <div>
            <center><h1>Driver Locations</h1></center>
            <table class="pf-c-table pf-m-grid-md" role="grid" aria-label="Driver Location Information" id="table-basic">
                <thead>
                <tr role="row">
                    <th role="columnheader" scope="col">Driver ID</th>
                    <th role="columnheader" scope="col">Latitude</th>
                    <th role="columnheader" scope="col">Longitude</th>
                    <th role="columnheader" scope="col">Grid ID</th>
                    <th role="columnheader" scope="col">Timestamp</th>
                </tr>
                </thead>
                {locations.map((loc) => (
                    <tbody role="rowgroup">
                    <tr role="row">
                        <td role="cell" data-label="Driver ID">{loc.driverId}</td>
                        <td role="cell" data-label="Latitude">{loc.latitude}</td>
                        <td role="cell" data-label="Longitude">{loc.longitude}</td>
                        <td role="cell" data-label="Grid ID">{loc.gridId}</td>
                        <td role="cell" data-label="Timestamp">{new Intl.DateTimeFormat("en-US", {
                            dateStyle: 'medium', timeStyle: 'long'
                        }).format(loc.timestamp)}</td>
                    </tr>
                    </tbody>
                ))}
            </table>
        </div>

    )
};

export default Locations