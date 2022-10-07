package com.mohaine.db.metadata;


import java.sql.SQLException;

public interface DbApply {
    void createTable(TableDef tableDef) throws SQLException;
    void createTable(String schemaName, TableDef tableDef) throws SQLException;
}

