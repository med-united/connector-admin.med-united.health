
FROM jboss/wildfly

COPY wildfly/modules/ /opt/jboss/wildfly/modules/
COPY wildfly/standalone.xml /opt/jboss/wildfly/standalone/configuration/

ADD target/frontend.war /opt/jboss/wildfly/standalone/deployments/