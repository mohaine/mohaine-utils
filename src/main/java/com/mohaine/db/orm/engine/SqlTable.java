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
public class SqlTable {
	private String tableName = null;
	private String alias = null;

	public SqlTable(String tableName, String alias) {
		this.tableName = tableName;
		this.alias = alias;
	}

	public SqlTable(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return String
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the tableName.
	 * 
	 * @param tableName
	 *            The tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return String
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 * 
	 * @param alias
	 *            The alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

}