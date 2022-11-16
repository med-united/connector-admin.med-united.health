
FROM quay.io/wildfly/wildfly-runtime-jdk17

COPY --chown=jboss:root target/server $JBOSS_HOME

RUN chmod -R ug+rwX $JBOSS_HOME