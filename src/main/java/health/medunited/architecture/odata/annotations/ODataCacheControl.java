package health.medunited.architecture.odata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to add
 * a CacheControl header to the EntitySet
 * request over the OData interface.
 * 
 * @see https://developer.mozilla.org/de/docs/Web/HTTP/Headers/Cache-Control
 * @author manuel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataCacheControl {
	/**
	 * The maximum time in seconds this response should be 
	 * cached by the client
	 * 
	 * @return max age in seconds
	 */
	int maxAge() default 0;
}
