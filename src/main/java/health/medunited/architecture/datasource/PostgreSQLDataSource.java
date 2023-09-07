package health.medunited.architecture.datasource;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.sql.DataSource;
import javax.ws.rs.Produces;

@Singleton
@DataSourceDefinition(
        name = "java:jboss/app/jdbc/primary",
        className = "org.postgresql.xa.PGXADataSource",
        user = "${env.DB_USER}",
        password = "${env.DB_PASSWORD}",
        serverName = "${env.DB_HOST}",
        portNumber = 5432,
        databaseName = "${env.DB_NAME}"
)

public class PostgreSQLDataSource{
    @Resource(lookup = "java:app/jdbc/primary")
    private DataSource dataSource;

    @Produces
    public DataSource getDatasource() {
        return dataSource;
    }
}