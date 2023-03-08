package com.mohaine.json;

import com.mohaine.util.CreateNewObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JsonObjectHandlerBase<T> implements JsonObjectHandler<T> {

    private Map<String, JsonObjectPropertyHandler<T, ?>> fieldHandlers = new HashMap<String, JsonObjectPropertyHandler<T, ?>>();

    {
        List<JsonObjectPropertyHandler<T, ?>> propertyHandlers = getPropertyHandlers();
        if (propertyHandlers != null) {
            for (JsonObjectPropertyHandler<T, ?> jsonObjectPropertyHandler : propertyHandlers) {
                fieldHandlers.put(jsonObjectPropertyHandler.getName(), jsonObjectPropertyHandler);
            }
        }
    }

    public abstract Class<T> getType();

    public abstract boolean handlesType(Class<?> value);

    public abstract List<JsonObjectPropertyHandler<T, ?>> getPropertyHandlers();


    protected String encodeBoolean(Boolean bool) {
        if (bool != null && bool.booleanValue()) {
            return "true";
        }
        return "false";
    }

    protected Boolean decodeBoolean(String str) {
        return "true".equals(str) ? Boolean.TRUE : Boolean.FALSE;
    }


    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public T fromJson(JsonUnknownObject unknownObject, JsonConverterConfig config) throws Exception {
        var namesToValues = new HashMap<String, Object>();

        List<JsonObjectPropertyHandler<T, ?>> propertyHandlers = getPropertyHandlers();
        if (propertyHandlers != null) {
            for (JsonObjectPropertyHandler<T, ?> ph : propertyHandlers) {
                JsonObjectPropertyHandler<T, Object> phT = (JsonObjectPropertyHandler<T, Object>) ph;

                Object property = unknownObject.getProperty(phT.getName());
                if (property instanceof JsonUnknownObject) {
                    if (phT.isJson()) {
                        StringBuilder sb = new StringBuilder();
                        new JsonEncoder(config).appendObject(sb, property);
                        property = sb.toString();
                    } else {
                        Class<Object> expectedType = (Class<Object>) phT.getExpectedType();
                        if (expectedType != null) {
                            property = config.convertToObject((JsonUnknownObject) property, expectedType);
                        }
                    }
                } else if (property instanceof List) {
                    Class<Object> expectedType = (Class<Object>) phT.getGenericType();
                    if (expectedType != null) {
                        List list = (List) property;
                        for (int i = 0; i < list.size(); i++) {
                            Object listObj = list.get(i);
                            if (listObj instanceof JsonUnknownObject) {
                                listObj = config.convertToObject((JsonUnknownObject) listObj, expectedType);
                                list.set(i, listObj);
                            }
                        }
                    }
                }
                namesToValues.put(phT.getName(), phT.mapValue(property));
            }
        }
        var newObj = createNewObject(namesToValues);

        if (propertyHandlers != null) {
            for (JsonObjectPropertyHandler<T, ?> ph : propertyHandlers) {
                JsonObjectPropertyHandler<T, Object> phT = (JsonObjectPropertyHandler<T, Object>) ph;

                if (newObj.unhandledNames().contains(phT.getName())) {
                    phT.setValue(newObj.obj(), namesToValues.get(phT.getName()));
                }
            }
        }

        return newObj.obj();
    }


    protected abstract CreateNewObject<T> createNewObject(HashMap<String, Object> namesToValues);

    public JsonUnknownObject toJson(T value, JsonConverterConfig jsonConverterConfig) {
        JsonUnknownObject obj = new JsonUnknownObject();
        List<JsonObjectPropertyHandler<T, ?>> propertyHandlers = getPropertyHandlers();
        if (propertyHandlers != null) {
            for (JsonObjectPropertyHandler<T, ?> ph : propertyHandlers) {
                @SuppressWarnings("unchecked")
                JsonObjectPropertyHandler<T, Object> phT = (JsonObjectPropertyHandler<T, Object>) ph;
                Object fieldValue = phT.getValue(value);
                if (phT.isJson() && fieldValue instanceof String) {
                    String fv = (String) fieldValue;
                    obj.setProperty(phT.getName(), fv);
                } else {
                    obj.setProperty(phT.getName(), fieldValue);
                }
            }
        }
        return obj;
    }
}
