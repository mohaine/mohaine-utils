package com.mohaine.db.metadata;

public record ColumnDef(String name, Type type, Boolean key, Boolean indexed, String defaultValue, Boolean nullable,
                        String remarks, Integer position, Boolean autoincrement) {


    public ColumnDef(String name, Type type, Integer position) {
        this(name, type, false, false, null, false, null, position, false);
    }


    public enum DataType {
        ID, DateTime, String, Int, Boolean, Decimal, Clob, Long, Blob, Unknown;

        public boolean isTicked() {
            return this == Clob || this == String;
        }
    }

    public record Type(ColumnDef.DataType type, Integer size, Integer maxSize) {
        public Type(ColumnDef.DataType type) {
            this(type, null, null);
        }
    }


}
