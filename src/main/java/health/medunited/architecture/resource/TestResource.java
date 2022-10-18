package health.medunited.architecture.resource;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventService;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.provider.ConnectorScope;
import health.medunited.architecture.service.StatusService;
import health.medunited.architecture.service.endpoint.SSLUtilities;
import health.medunited.architecture.z.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.regex.PatternSyntaxException;


@Path("status")
public class TestResource {

    @Inject
    DefaultConnectorServicesProvider defaultConnectorServicesProvider;

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    SecretsManagerService secretsManagerService;

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @Inject
    Provider<ConnectorScope> connectorScopeProvider;

    @Inject
    StatusService statusService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@HeaderParam("Url") String url,
                           @HeaderParam("MandantId") String mandantId,
                           @HeaderParam("ClientSystemId") String clientSystemId,
                           @HeaderParam("WorkplaceId") String workplaceId,
                           @HeaderParam("UserId") String userId,
                           @HeaderParam("ClientCertificate") String clientCertificate,
                           @HeaderParam("ClientCertificatePassword") String clientCertificatePassword) throws FaultMessage, CertificateException, URISyntaxException, KeyStoreException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {

        ConnectorScope connectorScope = connectorScopeProvider.get();

        ContextType contextType = new ContextType();
        contextType.setClientSystemId(clientSystemId);
        contextType.setMandantId(mandantId);
        contextType.setWorkplaceId(workplaceId);
        contextType.setUserId(userId);

        ConnectorScopeContext connectorScopeContext = new ConnectorScopeContext(url, contextType, clientCertificate, clientCertificatePassword);

        connectorScope.setConnectorScopeContext(connectorScopeContext);

        statusService.getStatus(httpServletRequest);
        return Response.ok().build();
    }

}
