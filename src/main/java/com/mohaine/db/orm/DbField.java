package com.mohaine.db.orm;

import java.lang.annotation.*;

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
