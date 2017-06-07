package com.mohaine.db.orm.engine;

public interface LoadFieldDefinition {

	/**
	 * Sets value from column into object
	 * 
	 * @param object field name
	 * @param value field value
	 */
	public abstract void setValue(Object object, Object value);

	/**
	 * get the column name for this field
	 * 
	 * @return String 
	 */
	public abstract String getColumnName();

}