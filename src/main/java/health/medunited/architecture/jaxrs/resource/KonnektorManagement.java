package health.medunited.architecture.jaxrs.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import health.medunited.architecture.entities.Konnektor;
import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;

import javax.ws.rs.*;

@Path("konnektor")
public class KonnektorManagement {

    @Inject
    private KonnektorService konnektorService;

    @Inject
    @Named("secunet")
    private Connector secunetConnector;

    @Inject
    @Named("kocobox")
    private Connector kocoboxConnector;

    @Inject
    @Named("rise")
    private Connector riseConnector;

    private Map<String, Connector> connectorMap;

    @PostConstruct
    public void init() {
        this.connectorMap = new HashMap<>();
        connectorMap.put(ConnectorBrands.SECUNET.getValue(), secunetConnector);
        connectorMap.put(ConnectorBrands.KOCOBOX.getValue(), kocoboxConnector);
        connectorMap.put(ConnectorBrands.RISE.getValue(), riseConnector);
    }

    @PersistenceContext(unitName = "dashboard")
    private EntityManager em;

    @POST
    @Transactional
    @Path("/{connectorType}/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createKonnektor(Konnektor konnektor) {
        // em.persist(konnektor);
        konnektorService.create(konnektor);
        return Response.status(Response.Status.CREATED).entity(konnektor).build();
    }

    @POST
    @Transactional
    @Path("/{connectorType}/unique/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createKonnektorUniqueUrl(Konnektor konnektor) {
        // em.persist(konnektor);
        // konnektorService.createUniqueUrl(konnektor);
        // return Response.status(Response.Status.CREATED).entity(konnektor).build();
        boolean success = konnektorService.createUniqueUrl(konnektor);
        if (success) {
            return Response.status(Response.Status.CREATED).entity(konnektor).build();
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Cannot be created, URL exists already\"}")
                    .build();
        }
    }
    @GET
    @Path("/{connectorType}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllKonnektors() {
        List<Konnektor> konnektors = em.createQuery("SELECT k FROM Konnektor k", Konnektor.class).getResultList();
        GenericEntity<List<Konnektor>> entity = new GenericEntity<List<Konnektor>>(konnektors) {
        };
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @GET
    @Path("/{connectorType}/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConnectorInfo() {
        Map<String, Long> info = konnektorService.getConnectorInfo();
        return Response.status(Response.Status.OK).entity(info).build();
    }
    
}