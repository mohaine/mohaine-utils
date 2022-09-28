package com.mohaine.db.migrate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DbMetadata {
	private List<TableDef> tables;

	private String version;

	public DbMetadata() {

	}

	public DbMetadata(List<TableDef> tables) {
		this.tables = tables;
		sortTables();
	}

	public void sortTables() {
		Collections.sort(this.tables, new Comparator<TableDef>() {
			@Override
			public int compare(TableDef o1, TableDef o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
			}
		});
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<TableDef> getTables() {
		return tables;
	}

	public void setTables(List<TableDef> metadata) {
		this.tables = metadata;
	}

	public TableDef findTable(String tableName) {
		for (TableDef table : tables) {
			if (table.getName().equalsIgnoreCase(tableName)) {
				return table;
			}
		}

		return null;
	}
}
