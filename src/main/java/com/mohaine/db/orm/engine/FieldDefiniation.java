package com.mohaine.db.orm.engine;

public interface FieldDefiniation extends LoadFieldDefinition {

	public Object getValue(Object object);

	public String getModifyBind();

	public boolean isPostSelectKey();

	public String getSequenceName();
}