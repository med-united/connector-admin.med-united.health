package health.medunited.architecture.util;


import health.medunited.architecture.entities.RuntimeConfig;

public class RuntimeConfigExtractor {

    private RuntimeConfigExtractor() {
        throw new IllegalStateException("Utility class");
    }

    public static RuntimeConfig extractRuntimeConfigFromHeaders(String clientSystemId, String clientCertificate,
                                                                String clientCertificatePassword, String host, String port,
                                                                String vzdPort, String mandantId, String workplaceId,
                                                                String userId) {
        return new RuntimeConfig(host, port, vzdPort, mandantId, clientSystemId, workplaceId, userId, clientCertificate,
                clientCertificatePassword);
    }
}
