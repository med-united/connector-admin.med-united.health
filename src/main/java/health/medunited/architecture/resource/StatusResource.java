package health.medunited.architecture.resource;

import health.medunited.architecture.service.CardService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("status")
public class StatusResource {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(StatusResource.class.getName());

    @Inject
    CardService cardService;

    @GET
    public String getStatus(@HeaderParam("Url") String url, @HeaderParam("TerminalId") String terminalId, @HeaderParam("TerminalPassword") String terminalPassword) {
        LOG.info(cardService.getUrl());
        return "OK";
    }
}
