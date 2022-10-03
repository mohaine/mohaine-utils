package com.mohaine.db.metadata;

import com.mohaine.json.Json;

import java.util.List;


public record SchemaDef(List<TableDef> tables) {

    public TableDef findTable(String tableName) {
        for (TableDef table : tables) {
            if (table.name().equalsIgnoreCase(tableName)) {
                return table;
            }
        }
        return null;
    }

    public String toJson() {
        try {
            return Json.encode(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SchemaDef fromJson(String json) {
        try {
            return Json.decode(json, SchemaDef.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
