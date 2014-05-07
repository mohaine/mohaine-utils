package com.mohaine.db.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface DbField {

    String sequenceName() default "";

    String columnName() default "";

    String modifyBind() default "";

    DbType type();

    boolean loadOnly() default false;

    boolean key() default false;

    boolean postSelectKey() default false;

    boolean parentKey() default false;

    boolean callSetter() default false;

}
