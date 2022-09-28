package com.mohaine.db.metadata;

public record ColumnDef(String name, Type type, Integer length, boolean key, boolean indexed, String defaultValue,
                        boolean notNull) {

    public ColumnDef(String name, Type type) {
        this(name, type, null);
    }

    public ColumnDef(String name, Type type, Integer length) {
        this(name, type, length, false, false, null, false);
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Type getType() {
        return type;
    }

    public Integer getLength() {
        return length;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public boolean isKey() {
        return key;
    }

    public boolean isNotNull() {
        return notNull;
    }


    public enum Type {
        ID, DateTime, String, Int, Boolean, Decimal, Clob, Long, Blob, Unknown
    }


}
