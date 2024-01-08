package health.medunited.architecture.odata;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataResponse.ODataResponseBuilder;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.core.ep.util.CircleStreamBuffer;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPADefaultProcessor;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmMapping;

import health.medunited.architecture.io.Util;
import health.medunited.architecture.odata.annotations.ODataCacheControl;

public class ETagCacheControlODataJPAProcessor extends ODataJPADefaultProcessor {

  private static final Logger log =
      Logger.getLogger(ETagCacheControlODataJPAProcessor.class.getName());

  private static final String CACHE_CONTROL_HEADER = "Cache-Control";

  public ETagCacheControlODataJPAProcessor(ODataJPAContext oDataJPAContext) {
    super(oDataJPAContext);
  }

  @Override
  public ODataResponse readEntitySet(final GetEntitySetUriInfo uriParserResultView,
      final String contentType) throws ODataException {
    ODataResponse oDataResponse = null;
    try {
      oDataJPAContext.setODataContext(getContext());
      List<Object> jpaEntities = jpaProcessor.process(uriParserResultView);
      oDataResponse = responseBuilder.build(uriParserResultView, jpaEntities, contentType);
      oDataResponse = processCacheControlHeader(uriParserResultView, oDataResponse);
    } finally {
      close();
    }
    return oDataResponse;
  }

  @Override
  public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriParserResultView,
      final String contentType) throws ODataException {
    ODataResponse oDataResponse = null;
    try {
      oDataJPAContext.setODataContext(getContext());
      long jpaEntityCount = jpaProcessor.process(uriParserResultView);
      oDataResponse = responseBuilder.build(jpaEntityCount);
      oDataResponse = processCacheControlHeader(uriParserResultView, oDataResponse);
    } finally {
      close();
    }
    return oDataResponse;
  }

  ODataResponse processCacheControlHeader(GetEntitySetCountUriInfo uriParserResultView,
      ODataResponse oDataResponse) throws EdmException {
    EdmType edmType = uriParserResultView.getTargetType();
    return processCacheControlHeaderForType(oDataResponse, edmType);
  }

  /**
   * Add a cache-control header if the given target type has the annotation ODataCacheControl set
   * and return a new ODataResponse
   *
   * @param uriParserResultView
   * @param oDataResponse
   * @return
   * @throws EdmException
   */
  ODataResponse processCacheControlHeader(final GetEntitySetUriInfo uriParserResultView,
      ODataResponse oDataResponse) throws EdmException {
    EdmType edmType = uriParserResultView.getTargetType();
    return processCacheControlHeaderForType(oDataResponse, edmType);
  }

  private ODataResponse processCacheControlHeaderForType(ODataResponse oDataResponse,
      EdmType edmType) throws EdmException {
    if (edmType instanceof EdmEntityType) {
      EdmMapping edmMapping = ((EdmEntityType) edmType).getMapping();
      if (edmMapping instanceof JPAEdmMapping) {
        Class<?> jpaClass = ((JPAEdmMapping) edmMapping).getJPAType();
        ODataCacheControl oDataCacheControl = jpaClass.getAnnotation(ODataCacheControl.class);
        if (oDataCacheControl != null) {
          oDataResponse = ODataResponse.fromResponse(oDataResponse)
              .header(CACHE_CONTROL_HEADER, "max-age=" + oDataCacheControl.maxAge()).build();
        }
      }
    }
    return oDataResponse;
  }

  @Override
  public ODataResponse executeBatch(final BatchHandler handler, final String contentType,
      final InputStream content) throws ODataException {
    try {
      oDataJPAContext.setODataContext(getContext());

      ODataResponse batchResponse;
      List<BatchResponsePart> batchResponseParts = new ArrayList<>();
      PathInfo pathInfo = getContext().getPathInfo();
      EntityProviderBatchProperties batchProperties =
          EntityProviderBatchProperties.init().pathInfo(pathInfo).build();
      List<BatchRequestPart> batchParts =
          EntityProvider.parseBatchRequest(contentType, content, batchProperties);

      for (BatchRequestPart batchPart : batchParts) {
        BatchResponsePart batchResponsePart = handler.handleBatchPart(batchPart);
        if (batchPart.getRequests().size() == 1 && batchResponsePart.getResponses().size() == 1) {
          ODataResponseBuilder oDataResponseBuilder = ODataResponse.newBuilder();
          ODataResponse firstBatchResponseODataResponse = batchResponsePart.getResponses().get(0);

          if (firstBatchResponseODataResponse.getEntity() instanceof InputStream) {
            InputStream inputStream = firstBatchResponseODataResponse.getEntityAsStream();
            byte[] targetArray;
            try {
              targetArray = Util.getBytesFromInputStream(inputStream);
              inputStream.close();
              CircleStreamBuffer circleStreamBuffer = new CircleStreamBuffer();
              circleStreamBuffer.getOutputStream().write(targetArray);
              String etag = Util.getETagDigest(targetArray);
              ODataRequest oDataRequest = batchPart.getRequests().get(0);
              String ifNoneMatchHeader = oDataRequest.getRequestHeaderValue("If-None-Match");

              if ((ifNoneMatchHeader != null) && (ifNoneMatchHeader.equals(etag))) {
                oDataResponseBuilder.status(HttpStatusCodes.NOT_MODIFIED);
                if (firstBatchResponseODataResponse.containsHeader(CACHE_CONTROL_HEADER)) {
                  oDataResponseBuilder.header(CACHE_CONTROL_HEADER,
                      firstBatchResponseODataResponse.getHeader(CACHE_CONTROL_HEADER));
                }
                oDataResponseBuilder.header("Last-Modified",
                    oDataRequest.getRequestHeaderValue("If-Modified-Since"));
              } else {
                oDataResponseBuilder.status(firstBatchResponseODataResponse.getStatus());
                oDataResponseBuilder.eTag(etag);
                oDataResponseBuilder.header("Last-Modified",
                    DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
                oDataResponseBuilder.entity(circleStreamBuffer.getInputStream());
                for (String key : firstBatchResponseODataResponse.getHeaderNames()) {
                  oDataResponseBuilder.header(key, firstBatchResponseODataResponse.getHeader(key));
                }
              }
              batchResponsePart.getResponses().clear();
              batchResponsePart.getResponses().add(oDataResponseBuilder.build());
            } catch (IOException e) {
              log.log(Level.SEVERE, "Could no process etag, e");
            }
          }
        }
        batchResponseParts.add(batchResponsePart);
      }
      batchResponse = EntityProvider.writeBatchResponse(batchResponseParts);
      return batchResponse;
    } finally {
      close(true);
    }
  }

}
