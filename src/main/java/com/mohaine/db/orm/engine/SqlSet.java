package com.mohaine.db.orm.engine;

public class SqlSet {

	private Column column = null;
	private String bind = null;

	public SqlSet(Column column, String bind) {
		this.column = column;
		this.bind = bind;
	}

	/**
	 * @return Object
	 */
	public Column getColumn() {
		return column;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value The value to set
	 */
	public void setColumn(Column value) {
		this.column = value;
	}

	public String getBind() {
		return bind;
	}

	public void setBind(String bind) {
		this.bind = bind;
	}

}