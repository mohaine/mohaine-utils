package com.mohaine.db.metadata;


public interface DbShim {

    SchemaMetadata loadMetadata() throws Exception;

    void updateStructureToMatch(SchemaMetadata metadata) throws Exception;

    void recreateTable(TableDef tableDef) throws Exception;

    public void dropTable(TableDef tableDef) throws Exception;

    public String getDbColumnAlter(ColumnDef columnDef);

    public String getDbColumnAdd(ColumnDef columnDef);

    void appendColumnName(StringBuilder sb, String name);

    void appendTableName(StringBuilder sb, String name);

}
