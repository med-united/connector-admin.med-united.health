// No Security Mechanism added yet, (controller can be accessed without credentials)



// package health.medunited.architecture.jaxrs.resource;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import javax.annotation.PostConstruct;
// import javax.inject.Inject;
// import javax.inject.Named;
// import javax.persistence.EntityManager;
// import javax.persistence.PersistenceContext;
// import javax.transaction.Transactional;
// import javax.ws.rs.core.GenericEntity;
// import javax.ws.rs.core.MediaType;
// import javax.ws.rs.core.Response;

// import health.medunited.architecture.entities.KonnektorUpdate;
// import health.medunited.architecture.entities.UpdateStatus;
// import health.medunited.architecture.jaxrs.management.Connector;
// import health.medunited.architecture.jaxrs.management.ConnectorBrands;

// import javax.ws.rs.*;

// @Path("update")
// public class UpdateManagement {

//     @Inject
//     @Named("secunet")
//     private Connector secunetConnector;

//     @Inject
//     @Named("kocobox")
//     private Connector kocoboxConnector;

//     @Inject
//     @Named("rise")
//     private Connector riseConnector;

//     private Map<String, Connector> connectorMap;

//     @PostConstruct
//     public void init() {
//         this.connectorMap = new HashMap<>();
//         connectorMap.put(ConnectorBrands.SECUNET.getValue(), secunetConnector);
//         connectorMap.put(ConnectorBrands.KOCOBOX.getValue(), kocoboxConnector);
//         connectorMap.put(ConnectorBrands.RISE.getValue(), riseConnector);
//     }

//     @PersistenceContext(unitName = "dashboard")
//     private EntityManager em;

//     @POST
//     @Transactional
//     @Path("/{connectorType}/")
//     @Consumes(MediaType.APPLICATION_JSON)
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response createKonnektorUpdate(KonnektorUpdate konnektorUpdate) {
//         em.persist(konnektorUpdate);
//         return Response.status(Response.Status.CREATED).entity(konnektorUpdate).build();
//     }

//     @GET
//     @Path("/{connectorType}/{id}")
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response findKonnektorUpdatebyID(
//             @PathParam("id") String id) {
//         KonnektorUpdate konnektorUpdate = em.find(KonnektorUpdate.class, id);

//         if (konnektorUpdate == null) {
//             throw new IllegalArgumentException("No KonnektorUpdate found for ID: " + id);
//         }

//         return Response.status(Response.Status.OK).entity(konnektorUpdate).build();
//     }

//     @GET
//     @Path("/{connectorType}/all")
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response findAllKonnektorUpdate(@PathParam("connectorType") String connectorType) {

//         List<KonnektorUpdate> konnektorUpdates = em
//                 .createQuery("SELECT k FROM KonnektorUpdate k", KonnektorUpdate.class).getResultList();

//         if (konnektorUpdates == null || konnektorUpdates.isEmpty()) {
//             throw new IllegalArgumentException("No KonnektorUpdates found for connector type: " + connectorType);
//         }

//         GenericEntity<List<KonnektorUpdate>> entity = new GenericEntity<List<KonnektorUpdate>>(konnektorUpdates) {
//         };
//         return Response.status(Response.Status.OK).entity(entity).build();
//     }

//     @POST
//     @Transactional
//     @Path("/{connectorType}/UpdateStatus/")
//     @Consumes(MediaType.APPLICATION_JSON)
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response createUpdateStatus(UpdateStatus updateStatus) {
//         em.persist(updateStatus);
//         return Response.status(Response.Status.CREATED).entity(updateStatus).build();
//     }

//     @GET
//     @Path("/{connectorType}/UpdateStatus/all")
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response findAllUpdateStatus(@PathParam("connectorType") String connectorType) {

//         List<UpdateStatus> updateStatus = em
//                 .createQuery("SELECT k FROM UpdateStatus k", UpdateStatus.class).getResultList();

//         if (updateStatus == null || updateStatus.isEmpty()) {
//             throw new IllegalArgumentException("No UpdateStatus found for connector type: " + connectorType);
//         }

//         GenericEntity<List<UpdateStatus>> entity = new GenericEntity<List<UpdateStatus>>(updateStatus) {
//         };
//         return Response.status(Response.Status.OK).entity(entity).build();
//     }

//     @GET
//     @Path("/{connectorType}/UpdateStatus/{id}")
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response findUpdateStatusID(
//             @PathParam("id") String id) {
//         UpdateStatus updateStatus = em.find(UpdateStatus.class, id);

//         if (updateStatus == null) {
//             throw new IllegalArgumentException("No UpdateStatus found for ID: " + id);
//         }

//         return Response.status(Response.Status.OK).entity(updateStatus).build();
//     }

// }
