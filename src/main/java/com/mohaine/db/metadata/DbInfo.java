package com.mohaine.db.metadata;


public interface DbInfo {

    SchemaDef loadSchema(String schema) throws Exception;

    TableDef loadTable(String schema, String tableName) throws Exception;
}

