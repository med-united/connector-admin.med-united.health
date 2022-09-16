# connector-admin.med-united.health

A tool where you can maintain all the runtime configurations for all the doctors and their connectors that are using the ere-ps-app

# How to run this application?

Prerequisites:

1. Java 18 SDK running on your machine
2. Maven 3 running on your machine (https://maven.apache.org/download.cgi)
3. Git running on your machine (https://git-scm.com/downloads)

```
mvn wildfly:start
mvn wildfly:deploy
```
# How to stop the application?
```
mvn wildfly:undeploy
mvn wildfly:shutdown
```
