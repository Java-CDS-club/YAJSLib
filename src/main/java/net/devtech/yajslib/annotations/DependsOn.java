package net.devtech.yajslib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * an annotation that states the persistent depends on a persistent being registered for a given type
 * is used for persistent validation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface DependsOn {
	Class<?>[] value();
}
