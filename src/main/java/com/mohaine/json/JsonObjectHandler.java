package com.mohaine.json;

public interface JsonObjectHandler<T> {


    boolean handlesType(Class<?> aClass);

    Class<T> getType();

    JsonUnknownObject toJson(T value, JsonConverterConfig jsonConverterConfig);

    T fromJson(JsonUnknownObject unknownObject, JsonConverterConfig jsonConverterConfig) throws Exception;
}
