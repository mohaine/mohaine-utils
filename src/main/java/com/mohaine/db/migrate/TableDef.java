package com.mohaine.db.migrate;

import java.util.List;


public class TableDef {

	private String name;

	private List<ColumnDef> columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ColumnDef> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnDef> columns) {
		this.columns = columns;
	}

}
