package health.medunited.architecture.odata.etag;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.apache.olingo.odata2.core.ep.util.CircleStreamBuffer;

import health.medunited.architecture.io.Util;

@Provider
public class ETagResponseFilter implements ContainerResponseFilter {
	
	@Context
	private Providers providers;

	// https://stackoverflow.com/questions/25332103/capture-response-payload-in-jax-rs-filter
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		byte[] arrayOfByte = payloadMessage(responseContext);
		if (arrayOfByte == null) {
			return;
		}
		if (arrayOfByte.length == 0) {
			return;
		}
		
		String etag = Util.getETagDigest(arrayOfByte);
		responseContext.getHeaders().add("ETag", etag);
		String ifNoneMatchHeader = requestContext.getHeaderString("If-None-Match");
		
		if ((ifNoneMatchHeader != null) && (ifNoneMatchHeader.equals(etag))) {
			responseContext.setStatus(304);
			responseContext.getHeaders().add("Last-Modified", requestContext.getHeaderString("If-Modified-Since"));
			responseContext.setEntity(null);
		} else {
			responseContext.getHeaders().add("Last-Modified", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
		}
	}
	
    private byte[] payloadMessage(ContainerResponseContext responseContext) throws IOException {
        if (responseContext.hasEntity()) {
        	Object entity = responseContext.getEntity();
        	if(entity instanceof InputStream) {
	        	InputStream initialStream = (InputStream) entity;
	        	byte[] targetArray = Util.getBytesFromInputStream(initialStream);
	            CircleStreamBuffer circleStreamBuffer = new CircleStreamBuffer();
	            circleStreamBuffer.getOutputStream().write(targetArray);
	            responseContext.setEntity(circleStreamBuffer.getInputStream());
	            initialStream.close();
	            return targetArray;
        	}
        }
        return new byte[] {};
    }

}