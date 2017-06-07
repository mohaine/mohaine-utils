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
 */
public class Where {

	private Column column = null;

	public Where(Column column) {
		this.column = column;
	}

	/**
	 * @return Object
	 */
	public Column getColumn() {
		return column;
	}

	/**
	 * Sets the value1.
	 * 
	 * @param value The value1 to set
	 */
	public void setColumn(Column value) {
		this.column = value;
	}
}