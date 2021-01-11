/**
 * 
 */
package board.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

/**
 * Indicates that the annotated method responds to HTTP PUT requests.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see HttpMethod
 * @since 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
@Documented
public @interface PATCH {
}

