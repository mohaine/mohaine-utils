package com.mohaine.json;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JsonEncoder {
    private JsonConverterConfig config;

    public JsonEncoder() {
        this.config = new JsonConverterConfig();
    }

    public JsonEncoder(JsonConverterConfig config) {
        super();
        this.config = config;
    }

    public void appendList(StringBuilder sb, Collection values) {
        sb.append("[");
        int count = 0;
        if (values != null) {
            for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                Object value = iter.next();
                int lengthBefore = sb.length();
                if (count > 0) {
                    sb.append(',');
                }
                boolean appendObject = appendObject(sb, value);
                if (appendObject) {
                    count++;
                } else {
                    sb.setLength(lengthBefore);
                }
            }
        }
        sb.append("]");
    }

    private void appendArray(StringBuilder sb, Object[] values) {
        sb.append("[");
        int count = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                Object value = values[i];
                int lengthBefore = sb.length();
                if (count > 0) {
                    sb.append(',');
                }
                boolean appendObject = appendObject(sb, value);
                if (appendObject) {
                    count++;
                } else {
                    sb.setLength(lengthBefore);
                }
            }
        }
        sb.append("]");
    }

    public void appendMap(StringBuilder sb, Map map) {
        sb.append("{");
        int count = 0;
        for (Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            if (count > 0) {
                sb.append(',');
            }
            appendString(sb, key);
            sb.append(':');
            if (!appendObject(sb, map.get(key))) {
                sb.append("null");
            }
            count++;

        }
        sb.append("}");
    }

    public void appendNamedValue(StringBuilder sb, String name, List values) {
        if (values == null) {
            sb.append('"');
            escapeStringForJson(sb, name);
            sb.append("\":null");
        } else {
            sb.append('"');
            escapeStringForJson(sb, name);
            sb.append("\":");
            appendList(sb, values);
        }

    }

    public void appendNamedValue(StringBuilder sb, String name, Map map) {
        if (map == null) {
            sb.append('"');
            escapeStringForJson(sb, name);
            sb.append("\":null");
        } else {
            sb.append('"');
            escapeStringForJson(sb, name);
            sb.append("\":");
            appendMap(sb, map);
        }

    }

    public void appendNamedValue(StringBuilder sb, String name, Object value) {
        appendString(sb, name);
        sb.append(':');
        if (!appendObject(sb, value)) {
            sb.append("null");
        }
    }

    public void appendNamedJsonValue(StringBuilder sb, String name, String value) {
        appendString(sb, name);
        sb.append(':');
        sb.append(value);
    }

    public boolean appendObject(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else {
            if (value instanceof String) {
                appendString(sb, (String) value);
            } else if (value instanceof Number) {
                sb.append(value.toString());
            } else if (value instanceof Boolean) {
                appendBoolean(sb, value);
            } else if (value instanceof Collection) {
                appendList(sb, (Collection) value);
            } else if (value instanceof Object[]) {
                appendArray(sb, (Object[]) value);
            } else if (value instanceof Map) {
                appendMap(sb, (Map) value);
            } else if (value instanceof Date) {
                appendDate(sb, (Date) value);
            } else {
                return toJson(sb, value);
            }
        }
        return true;
    }

    private void appendDate(StringBuilder sb, Date date) {
        if (date == null) {
            sb.append("null");
        }
        sb.append("\"\\/Date(");
        sb.append(date.getTime());
        sb.append(")\\/\"");
    }

    private void appendBoolean(StringBuilder sb, Object value) {
        if (((Boolean) value).booleanValue()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
    }

    public boolean toJson(StringBuilder sb, Object value) {
        ArrayList<JsonObjectHandler<?>> objectHandlers = config.getObjectHandlers();
        if (objectHandlers != null) {
            for (JsonObjectHandler handler : objectHandlers) {
                if (handler.handlesType(value.getClass())) {
                    JsonUnknownObject object = handler.toJson(value, config);
                    appendMap(sb, object.getProperties());
                    return true;
                }
            }
        }

        if (value instanceof JsonUnknownObject) {
            JsonUnknownObject object = (JsonUnknownObject) value;
            appendMap(sb, object.getProperties());
            return true;
        }
        return false;
    }

    public void appendNamedValue(StringBuilder sb, String name, boolean value) {
        appendString(sb, name);
        sb.append(':');
        appendBoolean(sb, value ? Boolean.TRUE : Boolean.FALSE);
    }


    public void appendAnotherNamedValue(StringBuilder sb, String name, Number value) {
        sb.append(',');
        appendNamedValue(sb, name, value);
    }

    public void appendNamedValue(StringBuilder sb, String name, Number value) {
        appendString(sb, name);
        sb.append(':');
        if (value == null) {
            sb.append("null");
        } else {
            sb.append(value.toString());
        }
    }

    public void appendAnotherNamedValue(StringBuilder sb, String name, boolean value) {
        sb.append(',');
        appendNamedValue(sb, name, value);
    }

    public void appendAnotherNamedValue(StringBuilder sb, String name, Object value) {
        sb.append(',');
        appendNamedValue(sb, name, value);
    }

    public void appendAnotherNamedValue(StringBuilder sb, String name, List value) {
        sb.append(',');
        appendNamedValue(sb, name, value);
    }

    public void appendAnotherNamedValue(StringBuilder sb, String name, String value) {
        sb.append(',');
        appendNamedValue(sb, name, value);
    }

    public void appendNamedValue(StringBuilder sb, String name, String value) {
        appendString(sb, name);
        sb.append(':');
        appendString(sb, value);
    }

    public void appendString(StringBuilder sb, String name) {
        if (name == null) {
            sb.append("null");
        } else {
            sb.append('"');
            escapeStringForJson(sb, name);
            sb.append("\"");
        }
    }

    private void escapeStringForJson(StringBuilder sb, String value) {

        if (value == null) {
            sb.append("null");
        } else {
            char[] toCharArray = value.toCharArray();
            for (int i = 0; i < toCharArray.length; i++) {
                char curChar = toCharArray[i];

                if (curChar == '"') {
                    sb.append('\\');
                    sb.append(curChar);
                } else if (curChar == '\'') {
                    sb.append('\\');
                    sb.append(curChar);
                } else if (curChar == '\\') {
                    sb.append('\\');
                    sb.append(curChar);
                } else if (curChar == '\b') {
                    sb.append("\\b");
                } else if (curChar == '\f') {
                    sb.append("\\f");
                } else if (curChar == '\n') {
                    sb.append("\\n");
                } else if (curChar == '\r') {
                    sb.append("\\r");
                } else if (curChar == '\t') {
                    sb.append("\\t");
                } else if (curChar > 255) {
                    sb.append("\\u");
                    String hex = Integer.toHexString(curChar);
                    for (int offset = hex.length(); offset < 4; offset++) {
                        sb.append('0');
                    }
                    sb.append(hex);
                } else {
                    sb.append(curChar);
                }

            }
        }
    }

}
