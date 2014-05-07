package com.mohaine.db.orm;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbTable {

	boolean saveParentFields() default false;

	boolean inheritParentKeys() default false;

	boolean inheritParentKeysAsField() default false;

	String tableName() default "";

}
