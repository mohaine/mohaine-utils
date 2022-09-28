package com.mohaine.db.migrate;

import java.util.Date;
import java.util.List;


public interface DbLoader {

	DbMetadata loadMetadata() throws Exception;

	void updateStructureToMatch(DbMetadata metadata) throws Exception;

	void recreateTable(TableDef tableDef) throws Exception;

	public void dropTable(TableDef tableDef) throws Exception;

	public String getDbColumnAlter(ColumnDef columnDef);

	public String getDbColumnAdd(ColumnDef columnDef);

	void appendColumnName(StringBuilder sb, String name);

	void appendTableName(StringBuilder sb, String name);

}
