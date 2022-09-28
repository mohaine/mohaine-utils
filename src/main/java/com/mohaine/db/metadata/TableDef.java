package com.mohaine.db.metadata;

import java.util.List;


public record TableDef(String name, List<ColumnDef> columns) {

    public String getName() {
        return name;
    }

    public List<ColumnDef> getColumns() {
        return columns;
    }
}
