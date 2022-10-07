package com.mohaine.db.metadata;

import com.mohaine.json.Json;

import java.util.List;


public record DatabaseDef(List<SchemaDef> schemas) {

    public SchemaDef findSchema(String name) {
        for (SchemaDef schemaDef : schemas) {
            if (schemaDef.name().equalsIgnoreCase(name)) {
                return schemaDef;
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

    public static DatabaseDef fromJson(String json) {
        try {
            return Json.decode(json, DatabaseDef.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
