package com.mohaine.db.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbTable {

	boolean saveParentFields() default false;

	boolean inheritParentKeys() default false;

	boolean inheritParentKeysAsField() default false;

	String tableName() default "";

}
