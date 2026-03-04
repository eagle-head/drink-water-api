package br.com.drinkwater.api.versioning;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a REST controller with its API version metadata. Used by {@link ApiVersionInterceptor} to
 * inject version-related response headers.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {

    String value();

    boolean deprecated() default false;

    String sunset() default "";

    String successorVersion() default "";
}
