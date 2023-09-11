
FROM bitnami/wildfly:26.1.3-debian-11-r18

COPY wildfly-configuration/standalone.xml /opt/bitnami/wildfly/standalone/configuration/
COPY wildfly-configuration/modules/org/postgresql/main/module.xml /opt/bitnami/wildfly/modules/system/layers/base/org/postgresql/main/
COPY wildfly-configuration/modules/org/postgresql/main/postgresql-42.5.0.jar /opt/bitnami/wildfly/modules/system/layers/base/org/postgresql/main/

ADD target/ROOT.war /opt/bitnami/wildfly/standalone/deployments/

USER root
RUN chown -R 1001:root /opt/bitnami/wildfly
RUN chmod -R g+w /opt/bitnami/wildfly

RUN chown 1001:root /opt/bitnami/wildfly/standalone/configuration/standalone.xml
RUN chmod g+w /opt/bitnami/wildfly/standalone/configuration/standalone.xml

USER 1001

CMD ["/opt/bitnami/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-Djboss.bind.address.management=0.0.0.0"]
