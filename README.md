# connector-admin.med-united.health

A tool where you can maintain all the runtime configurations for all the doctors and their connectors

# How to run this application?

Prerequisites:

1. Java 11 SDK running on your machine
2. Maven 3 running on your machine
3. Git running on your machine

Make sure to tweak the following configuration:

- persistence.xml file:
```
  <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://{YOUR_HOST}:5432/connector" />
  <property name="javax.persistence.jdbc.user" value="{YOUR_USER}" /> 
  <property name="javax.persistence.jdbc.password" value="{YOUR_PASSWORD}" /> 
```
- Uncomment the following lines in pom.xml:
```
<phase>package</phase>
```

Now you can start the application:
```
mvn wildfly:provision
mvn wildfly:start
mvn package
mvn wildfly:deploy
```
# How to stop the application?
```
mvn wildfly:undeploy
mvn wildfly:shutdown
```

# Basic auth info

The application is secured with basic auth. The specification is in the file src/main/webapp/WEB-INF/web.xml

To access the application you need to create a user in the wildfly server. To do so, you can use this script:
```
target/server/bin/add-user.sh -> Linux
target/server/bin/add-user.bat -> Windows
```
