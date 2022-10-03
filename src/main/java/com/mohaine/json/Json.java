package com.mohaine.json;

public class Json {


    public Json(JsonConverterConfig config) {
    }

    public static <T> T decode(String json, Class<T> tClass) throws Exception {
        JsonConverterConfig config = getJsonConverterConfig(tClass);
        return new JsonDecoder(config, json).parseJson(tClass);
    }

    public static <T> JsonConverterConfig getJsonConverterConfig(Class<T> tClass) throws Exception {
        JsonConverterConfig config = new JsonConverterConfig();
        ReflectionJsonHandler.buildAll(config, tClass);
        return config;
    }

    public static String encode(Object value) throws Exception {
        JsonConverterConfig config = getJsonConverterConfig(value.getClass());
        JsonEncoder encoder = new JsonEncoder(config);
        StringBuilder sb = new StringBuilder();
        encoder.appendObject(sb, value);
        return sb.toString();
    }
}
