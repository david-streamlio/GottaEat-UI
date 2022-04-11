# GottaEat-UI
User interface components for the GottaEat food delivery service application. The project consists of four distinct modules that depend upon on another to function properly. 

##How To Run
In order to run the demo you need to execute the following steps:

1. Update the Pulsar OAuth2 credentials file in ***ALL*** of the <code>/src/main/resources/META-INF/application.properties</code> files. Replace the value of the <code>pulsar.oauth2.credentials-url</code> property to point to your local OAuth2 credentials file that you downloaded from the SN Cloud.

2. Start the **driver-location-service** by changing into that module's subdirectory and running the following command: 

	<code>"mvn clean compile quarkus:dev"</code>
   
	This will start the web service, and you can verify that it is running by visiting [http://localhost:8080/index.html]() 


3. Next, you should start the **driver-simulator** by changing into that module's subdirectory and running the following command: 

	<code>"mvn clean compile quarkus:dev"</code>
	
	This will start generating synthetic driver location data. You can specify the number of drivers to simulate as well as the Latitude/Longitude of the starting centriod for the drivers.



   You can verify that this service is running by refreshing the [http://localhost:8080/index.html]() page to confirm new records are showing up based on the timestamps.


4. You can then start the **driver-tracker-websocket** service that provides real-time updates of the driver position by changing into that module's subdirectory and running the following command: 

	<code>"mvn clean compile quarkus:dev"</code>
	

   To track an individual driver you can visit [http://localhost:8005/driver-tracker.html]() 
   
   Or if you want to see all the drivers in a current geographical area, then you can visit [http://localhost:8005/grid-tracker.html]() 


5. Finally, you can start the **driver-location-analytics-service** by first changing into that module's subdirectory and running the following command: 

	<code>"mvn clean compile quarkus:dev"</code>
	
	This will start the REST service that calculates the Grid used by the front service, which must be started separately by changing into the <code>src/main/webapp</code> subdirectory and running <code>npm start</code> to launch the UI. 
	
	This should automatically open a web broswer pointing to [http://localhost:3000](http://localhost:3000) that shows an interactive map of the driver density by H3 grid.



## References
* [How to add Patternfly components to Quarkus](https://quarkus.io/blog/gui-react-patternfly/)
* [React Leaflet Documentation](https://react-leaflet.js.org/)
* [Leaflet Documentation](https://leafletjs.com/SlavaUkraini/reference.html#map-event)
* [Leaflet Examples](https://tomik23.github.io/leaflet-examples/)