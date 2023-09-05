package health.medunited.architecture.datasource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PostgreSQLDataSourceTest {

    private DataSource dataSource;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setServerNames(new String[]{System.getenv("DB_HOST")});
        pgDataSource.setPortNumbers(new int[]{5432});
        pgDataSource.setDatabaseName(System.getenv("DB_NAME"));
        pgDataSource.setUser(System.getenv("DB_USER"));
        pgDataSource.setPassword(System.getenv("DB_PASSWORD"));

        dataSource = pgDataSource;

        connection = dataSource.getConnection();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testDatabaseConnection() throws SQLException {
        assertNotNull("DataSource should not be null", dataSource);
        assertNotNull("Connection should not be null", connection);

        // Execute a simple query to test the connection
        String sql = "SELECT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            assertTrue("Query should return a result", resultSet.next());
        }
    }

    @Test
    public void testRuntimeConfigTableExists() throws SQLException {
        assertNotNull("DataSource should not be null", dataSource);
        assertNotNull("Connection should not be null", connection);

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "runtimeconfig", new String[]{"TABLE"});

        assertTrue("Table 'runtimeconfig' should exist", resultSet.next());
    }
}