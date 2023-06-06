package health.medunited.architecture;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import health.medunited.architecture.datasource.PostgreSQLDataSource;
import health.medunited.architecture.entities.RuntimeConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

@Singleton
@Startup
public class Bootstrap {

    private static final Logger log = Logger.getLogger(Bootstrap.class.getName());

    @PersistenceContext
    EntityManager em;

    @Inject
    PostgreSQLDataSource dataSource;

    @PostConstruct
    public void generateDemoData() {

        log.info("Attempt to create table");
        try (Connection connection = dataSource.getDatasource().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS test_table (number INTEGER, name VARCHAR(255))";
                statement.executeUpdate(createTableQuery);
                log.info("\n\ntest_table successfully created -.-.-.-.-.-");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table", e);
        }

        RuntimeConfig runtimeConfigSecunet = getRuntimeConfigSecunet();
        em.persist(runtimeConfigSecunet);
        RuntimeConfig runtimeConfigKops = getRuntimeConfigKops();
        em.persist(runtimeConfigKops);
        RuntimeConfig runtimeConfigRise = getRuntimeConfigRise();
        em.persist(runtimeConfigRise);
    }

    public static RuntimeConfig getRuntimeConfigKops() {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setId("94310cf3-1bc0-403a-8c2e-44ae2472f0d2");
        runtimeConfig.setUserId("kops-id");
        runtimeConfig.setUrl("http://172.31.26.17");
        // runtimeConfig.setUrl("https://localhost");
        runtimeConfig.setMandantId("Mandant1");
        runtimeConfig.setClientSystemId("ClientID1");
        runtimeConfig.setWorkplaceId("Workplace1");
        runtimeConfig.setUseSSL(true);
        runtimeConfig.setClientCertificate("data:application/x-pkcs12;base64,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");
        runtimeConfig.setClientCertificatePassword("123456");
        return runtimeConfig;
    }

    public static RuntimeConfig getRuntimeConfigSecunet() {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setId("a2f8d25b-a239-4b9f-945c-1e693c81e4ec");
        runtimeConfig.setUserId("secunet-id");
        runtimeConfig.setUrl("https://192.168.178.42");
        runtimeConfig.setMandantId("Incentergy");
        runtimeConfig.setClientSystemId("Incentergy");
        runtimeConfig.setWorkplaceId("1786_A1");
        runtimeConfig.setUseSSL(true);
        runtimeConfig.setClientCertificate("data:application/x-pkcs12;base64,MIACAQMwgAYJKoZIhvcNAQcBoIAkgASCA+gwgDCABgkqhkiG9w0BBwGggCSABIID6DCCBVIwggVOBgsqhkiG9w0BDAoBAqCCBPswggT3MCkGCiqGSIb3DQEMAQMwGwQUsKEOeyT+A3MT3WRjP46E5LSVojwCAwDIAASCBMgHnMdsUgs2bjrjwwbS5e/ZCf8d4Sx0XzaswWMXZ0O8fiA+D1w2C4aOrjS4eTXAOHWcNi/L7yuU5XmZkqwJ4Vu/XJ0HQyDPKp/cpNxOAd/9BCILpxonVm7ztY1tsXK6B8yJG8KHlxbNMsBWnwsliM9WUf7S+FWDeqVPSJw6dRfXMMogTULFkK9ACJYeQwsvfHuum3Qv+BWc+Y/xVJo5sJPS5/kryciioO4kplyYeEK3BOxFPGhYyahD4GO/UW4X4cerSLAr+NPbLbcgF5as2e6jytI/bxiVcUsOmQCAJaF26qq//oV9HfMCtGVaiSE5ynR0PCbzhpkCrqUcSTjyo6UXL8EAyPHM4SeVhMxsEVYbdqR9Dxw90ICNeDG1zikk4GEQev/IwBzlCpwawTS+yT+q0iANX0Crk+KLyy6HtyylBMsqDgWL+GORUijZDOhA2nJSGQECLp/sytweOxhiEJrVAYOBZGsViDoSvgLDk2afgs+qODZpNHwqqq8T0SKGHygF/IqSVhx3S8YrBvb2YnYoZwagNdMuXVpZo5m+cHEvprzTqzYvnv5Ac5SIoGyyqFfW+bFCIv18RdmrpnRkzuPdGR39T0ybol2Bg4wB8AOTcDjTuyPmZvgAtx2/AncLYamicXp96sKz7rzhpPngfqztp9I5x6M98bglLJgofwfN7p9lM9ygiIH1d/A9mdAu3VzYZ7DbOoV/l1I0hWezUuP/iutLYvTKCWNjs98z+qmmx9T9MP/C21PH8a0uHecXDyHs+Pu0WHfeNjHM06EZjUhxk8pjXA6HeKtO1hWJNX6GSIti2LH2RCfEKDeWWbT4ZV7eVhEvNHG1X6XYlNraXnMUTem62JFyisafnXmVoMRzpwZENvBygLznjQE9vwGe3mQnA/T2cVZkPQ3SQBCRm/kFC5lOZkDu0oDWsDRoxVVB1kgWGWnsYb76tFmGymQKcZZVEAzkcmzL7FwWCKMBvYwGrq5se7ikCqCQHBeZsC/KYqunYT3Y3DdNZDRXQp/kGlXdFTkZdX/NZjueDOqP+sucqhWy4dvqReOysk40mXlvj+6d5kvZIYb4Z6QsxDROY1Ap5eVy51Oxh68ywmu+KcLcRwaD7CXOD7zid3FT2eS8PdwPHaF/SvLexKKE02WJsMJbv8K+aCgl2rb+KRZEb+kG/nwK7odDxdeWRk02XLLVYegrSekGBIID6MHx+y///JKUl0Nhxm89FAKmJQwOAIP/BIIBbhkjWOapF2cAtdyJPvnxC9rRN60OK5IM6HD2a+CSWYRUoY3v0RqlHtB8m9a3Ve8d75Gb5OO0AtA7mxeEf5jPk9YkNaPlH9W3Qw7qysEmrqCgFH6VRacX7gcClkGCimNnp+VRKATh4bSdwg/2NPbY6PIC2Y4Pljnt5M18D7LRX2oNRZDQC0Ej+OLaSncjDZlB4psrdA8cuyEzpydF8VoSvk+Pb22kb9zQRFDg0UMjWgDBhSWTCZm4gJEozZxgRyLfc24z2psHbCcGRyvGtz+S0S0LCWr0A0AT/3upXKzZ6PCrcf1E2pWqAVg7PePH3lg0g0tqd/dfVLE25eESVKETbfO9UhgI6GAjTgrQhjB6sEdI7jIKmAmg2AC6ONYLSHNgDhlBvZRB7ilpcFT3KjFAMBkGCSqGSIb3DQEJFDEMHgoAdgBhAGwAaQBkMCMGCSqGSIb3DQEJFTEWBBQ0gn+WEEW2jqtu8ZFDpMrMy2XR3QAAAAAAADCABgkqhkiG9w0BBwaggDCAAgEAMIAGCSqGSIb3DQEHATApBgoqhkiG9w0BDAEGMBsEFHSdfN8iGJ3Wv75K6LAgKWlH9iORAgMAyACggASCA+im7Nnu0Y1QxslFJtLK/XhB5E+ldYrEKKO13/zxmK2igscj3EmHhquUKM9Ob8XuOv0U0CRPktRTCu+SAUOmqb8o8zvgGE6OT8GowsLFvLDA7k9hNAThtVx3jRG7W1PkbEvI7zL5h1WDC/caXJFWId+1wDJRrRAcLDwzCsnqpeSUiUZOfFGlkyGo2P8huZYfdkSnasBpPrxIZUygRU3qmhDbBpnPFmyn5CJxjFMsfWbIaPnkykR8RRc0l+FsLyj3sklo9k7o3vFtdY0nQUNOvgf670GEE3Fsd6atLr33z7dlAQu5U6uZEBIOo4Qr5Xc5H99wa/USvguiKo9RALXHPOgCvk4JEWqMlJxTpKf1IWZVBZDADly80hrILcfvSDxZTmHNxm49PjfblFCNvTxsNsYp049Zxu7//RQtf/Hmc31BYHs2MhidPCJs5H9/JUBWVvHF4hVYbOWbbBH5f+HYkyr/DdSFIIkPLhrbX0cT4ldGzXsnhdGKxOept+QmUnTAXpFWYoqFDRWupVcaKhfuYoAHRxUJmnBQ7fGQXrQhStbJ50Q2R0FObo3sSmbedCcWKD9D1GXTv9s7J+5yMatAqTkm7xmY0hmI1cKDqx1ZcSrl3HLKDBmtjNzNFs8UYp4t8cYuFKhGTlQRrP9QxrY0ByFDqLN/bQd2CyafaCj3vFK5oQzuPTobKAcEggHtrjb91YJSfL6x22Fwz3pyCcl5WPU33k36Ki+qKHw36BJXN9Ca6NqAgdu4cl6LK9mYz3HZEkWibFhio0xoJdArH+q+fqyAfYyC45FQiKIE+odEqDlhCl2cxUP7dYL7RDj7E+HEopL3v4WM2XId1CUWOcYJwqjnmbOlahuBVhwZef2djQN4TrP3OuSAY+Lt4qZgZ2KOuaVgjNNqBL16v6DIFKUJKCraI86t+0eRgr4tbDjkU9HjxJ5m8GYNonyHhv6+zvARbUT04RbEYuj31fkwn4R4Unw7pUtJB7rIUH9ljPZc/+/loL0Fs6Y/FNFb4EVqqPdSdFUC5/a+9FrASOAlebtVaIRk8F/mMRjwFt4nan2oWCoTc+X3v9pafaEEdPlY5f/9WOa2tr9FQpROwYyf7gJ2wVGo+32PO55mqTbje+9mLvLpMK0Fkl6DZrdiyheNj2ZS4NfTwmQ1loCrnlJdjsnRG5eVZTTn9PpLC5OGwjpII9QOr4ouEcmLUUjbqEij6Rzy8wiisMGs/oHrH26rJs9Frh4TgYESrLD0FFaO8s/ha7JrO6qpKOlBzoXdBHsuHWjlncipfJUM0n5gKMKkZAj/a+cHcbK8juYiJWm8TYX3qjJE1ZDpH9rJKnX6N9oqFQAAAAAAAAAAAAAAAAAAAAAAADA+MCEwCQYFKw4DAhoFAAQUdnEvbILSKagRVWeJJUGg8vKnZUEEFGy1SHud+6JHd/+/uxUwq+g/wgzcAgMBkAAAAA==");
        runtimeConfig.setClientCertificatePassword("N4rouwibGRhne2Fa");
        return runtimeConfig;
    }

    public static RuntimeConfig getRuntimeConfigRise() {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setUserId("rise-id");
        runtimeConfig.setUrl("https://192.168.178.75");
        runtimeConfig.setMandantId("m");
        runtimeConfig.setClientSystemId("c");
        runtimeConfig.setWorkplaceId("a");
        runtimeConfig.setUseSSL(true);
        runtimeConfig.setClientCertificate("data:application/x-pkcs12;base64,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");
        runtimeConfig.setClientCertificatePassword("Uq>nn#L_/z[._XjA");
        return runtimeConfig;
    }
}
