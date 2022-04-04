# GottaEat-UI

User interface components for the GottaEat food delivery service application

The project consists of four distinct modules that depend upon on another to function properly. In order to run the demo you need to execute the following steps:

1. Start the driver-location-service by changing into that module's sub-directory and running the following command "mvn clean compile quarkus:dev". This will start the web service, and you can verify that it is running by visiting http://localhost:8080/

2. Next, you should start the driver-simulator to start generating snythetic driver location data by changing into that module's sub-directory and running the following command "mvn clean compile quarkus:dev". . You can verify that this service is running by refreshing the http://localhost:8080/ page to confirm new records are showning up based on the timestamps.
 
3. You can then start the driver-tracker-websocket service that provides real-time updates of the driver position by changing into that module's sub-directory and running the following command "mvn clean compile quarkus:dev". . You can visit http://localhost:8005/ to confirm this. The page opens a websocket, and recevies updates of the driver position and adjusts the marker on the UI accordingly. 
 
4. Finally, you can start the driver-location-analytics-service by changing into that module's sub-directory and running the following command "mvn clean compile quarkus:dev". , to produce a heatmap of the driver density by geographical region. You can visit http://localhost:9000/heatmap.html to see the interative map. Currently, the dataset is hard-coded and a websocket needs to be added to get updates.
