Client service is intended to demonstrate simple http server with request processing via xml request-response model
Data is stored in in-memory database (Standalone H2 instance)

First run the build
mvn clean package && mvn assembly:single

Then navigate to target folder, there should be file called clientservice-1.0-SNAPSHOT-bin.zip

In order to start standalone H2 instance simply run 
java -cp clientservice-1.0-SNAPSHOT.jar ru.dmzadorin.h2.H2Server

In order to start client run
java -jar clientservice-1.0-SNAPSHOT.jar

If you want to override configuration properies simply pass jvm argument -Dconfig.location
java -jar clientservice-1.0-SNAPSHOT.jar -Dconfig.location=%PATH_TO_CONFIG%


