package com.mohaine.db.orm.engine.reflection;

import com.mohaine.db.orm.engine.KeyedObjectTableMapping;

import java.util.UUID;

public class ReflectionKeyedObjectTableMapping extends KeyedObjectTableMapping {

    Class<?> objectClass;

    public ReflectionKeyedObjectTableMapping(Class<?> objectClass) {
        super();
        this.objectClass = objectClass;
    }

    @Override
    public Object createNewObject() {
        try {
            return objectClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object generateKey() {
        return UUID.randomUUID().toString();
    }

}
