
FROM jboss/wildfly

RUN wildfly/bin/add-user.sh -a -u 'adminuser1' -p 'password1!' -g 'user'

COPY wildfly/modules/ /opt/jboss/wildfly/modules/
COPY wildfly-configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration/

ADD target/frontend.war /opt/jboss/wildfly/standalone/deployments/