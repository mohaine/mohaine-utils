package com.mohaine.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class JsonConverterConfig {

    private ArrayList<JsonObjectHandler<?>> objectHandlers;

    private boolean addTypes = true;

    public ArrayList<JsonObjectHandler<?>> getObjectHandlers() {
        return objectHandlers;
    }

    public void setObjectHandlers(ArrayList<JsonObjectHandler<?>> objectHandlers) {
        this.objectHandlers = objectHandlers;
    }

    public boolean isAddTypes() {
        return addTypes;
    }

    public void setAddTypes(boolean addTypes) {
        this.addTypes = addTypes;
    }

    public void addHandler(JsonObjectHandler<?> jsonObjectHandler) {
        if (objectHandlers == null) {
            objectHandlers = new ArrayList<JsonObjectHandler<?>>();
        }
        objectHandlers.add(jsonObjectHandler);
    }

    public void addHandlers(Collection<JsonObjectHandler<?>> jsonObjectHandlers) {
        if (objectHandlers == null) {
            objectHandlers = new ArrayList<JsonObjectHandler<?>>();
        }
        objectHandlers.addAll(jsonObjectHandlers);
    }


    public boolean canHandle(Class<?> class1) {
        var builtInTypes = new Class[]{String.class, Collection.class, Map.class, Number.class, Boolean.class, Enum.class};
        for (var c : builtInTypes) {
            if (c.isAssignableFrom(class1)) {
                return true;
            }
        }

        if (class1.isPrimitive()) {
            return true;
        }


        if (objectHandlers == null) {
            return false;
        }

        for (JsonObjectHandler<?> handler : objectHandlers) {
            if (handler.handlesType(class1)) {
                return true;
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public <T> T convertToObject(JsonUnknownObject unknownObject, Class<? extends T> class1) {
        for (JsonObjectHandler<?> handler : objectHandlers) {
            if (handler.handlesType(class1)) {
                return (T) convertToObject(unknownObject, handler);
            }
        }
        return null;
    }

    public <T> T convertToObject(JsonUnknownObject unknownObject, JsonObjectHandler<T> handler) {
        try {
            T obj = handler.fromJson(unknownObject, this);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
