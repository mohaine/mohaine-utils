package com.mohaine.db.metadata;

import java.util.List;


public record TableDef(String name, List<ColumnDef> columns) {
    public ColumnDef findColumn(String name) {
        for (var i : columns) {
            if (i.name().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return null;
    }
}
