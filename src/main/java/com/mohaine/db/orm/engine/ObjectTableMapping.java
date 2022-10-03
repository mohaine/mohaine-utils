package com.mohaine.db.orm.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectTableMapping {

    private String tableName;

    private final List<LoadFieldDefinition> fields = new ArrayList<LoadFieldDefinition>();

    public void addField(LoadFieldDefinition def) {
        fields.add(def);
    }

    public abstract Object createNewObject();

    public List<LoadFieldDefinition> getFields() {
        return fields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public abstract Object generateKey();
}
