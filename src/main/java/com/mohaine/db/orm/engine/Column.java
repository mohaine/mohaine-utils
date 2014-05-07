/*
 * Created on Mar 22, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mohaine.db.orm.engine;

/**
 * @author graessle
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments
 */
public class Column {
	private String columnName = null;

	public Column(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return String
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Sets the columnName.
	 * 
	 * @param columnName
	 *            The columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}