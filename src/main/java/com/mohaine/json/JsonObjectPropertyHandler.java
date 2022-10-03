package com.mohaine.json;

public abstract class JsonObjectPropertyHandler<T, F> {
    public abstract String getName();

    public abstract F getValue(T object);

    public abstract void setValue(T object, F value);

    public abstract Class<?> getGenericType();

    public abstract Class<?> getExpectedType();

    public boolean isJson() {
        return false;
    }

    public abstract F mapValue(F value);
}
