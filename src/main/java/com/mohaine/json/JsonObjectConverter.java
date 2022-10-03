package com.mohaine.json;

import java.util.Collection;

public class JsonObjectConverter {
    public static final String TYPE = "__type__";

    private JsonConverterConfig config = new JsonConverterConfig();

    public JsonObjectConverter() {
        this(true);
    }

    public JsonObjectConverter(boolean addTypes) {
        super();
        config.setAddTypes(addTypes);
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(String jsonString) {
        return (T) new JsonDecoder(config, jsonString).parseJson();
    }

    public String encode(Object obj) {
        JsonEncoder jc = new JsonEncoder(config);
        StringBuilder sb = new StringBuilder();
        jc.appendObject(sb, obj);
        return sb.toString();
    }

    public void addHandler(JsonObjectHandler<?> jsonObjectHandler) {
        config.addHandler(jsonObjectHandler);
    }

    public void addHandlers(Collection<JsonObjectHandler<?>> jsonObjectHandlers) {
        config.addHandlers(jsonObjectHandlers);
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(String json, Class<? extends T> class1) {
        JsonDecoder jsonDecoder = new JsonDecoder(config, json);
        Object parseJson = jsonDecoder.parseJson();
        if (parseJson instanceof JsonUnknownObject) {
            parseJson = config.convertToObject((JsonUnknownObject) parseJson, class1);
        }
        return (T) parseJson;
    }

}
