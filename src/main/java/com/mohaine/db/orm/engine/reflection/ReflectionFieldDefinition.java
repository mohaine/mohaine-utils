package com.mohaine.db.orm.engine.reflection;

import com.mohaine.db.orm.DbType;
import com.mohaine.db.orm.engine.AbstractFieldDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionFieldDefinition extends AbstractFieldDefinition {
    private final DbType dbType;
    private Method setter;
    private final Field field;

    public ReflectionFieldDefinition(String columnName, Field field,
                                     DbType dbType) {
        super(columnName);
        this.field = field;
        field.setAccessible(true);
        this.dbType = dbType;
    }

    @Override
    public void setValue(Object object, Object value) {
        try {
            Object valueToSet = ReflectionLoadFieldDefinition.getValueToSet(
                    field, dbType, value);
            if (this.setter != null) {
                this.setter.invoke(object, valueToSet);
            } else {
                field.set(object, valueToSet);
            }
        } catch (ClassCastException e) {
            throw new RuntimeException(
                    "IllegalArgumentException setting field: "
                            + getColumnName() + " on class "
                            + field.getDeclaringClass().getName(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "IllegalArgumentException setting field: "
                            + getColumnName() + " on class "
                            + field.getDeclaringClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException setting field: "
                    + getColumnName() + " on class "
                    + field.getDeclaringClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                    "InvocationTargetException setting field: "
                            + getColumnName() + " on class "
                            + field.getDeclaringClass().getName(), e);
        }

    }

    public Object getValue(Object object) {
        try {
            return field.get(object);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "IllegalArgumentException getting field: "
                            + getColumnName() + " on class "
                            + field.getDeclaringClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException getting field: "
                    + getColumnName() + " on class "
                    + field.getDeclaringClass().getName(), e);
        }
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

}
