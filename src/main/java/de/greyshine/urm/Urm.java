package de.greyshine.urm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for (web-controller) methods to be restricted reagarding user roles.   
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( value=ElementType.METHOD ) 
public @interface Urm {

	/**
	 * When no role is given, then any user must be logged in.
	 * @return which rights are allowed to proceed the annotation method
	 */
	String[] value();
	
}
