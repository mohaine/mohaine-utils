package com.mohaine.db.orm.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author graesslem
 * 
 */
public abstract class KeyedObjectTableMapping extends ObjectTableMapping {

	private FieldDefiniation parentKey = null;

	public void setParentKey(FieldDefiniation def) {
		parentKey = def;
	}

	public FieldDefiniation getParentKey() {
		return parentKey;
	}

	private final List<FieldDefiniation> keys = new ArrayList<FieldDefiniation>();

	public void addKey(FieldDefiniation def) {
		keys.add(def);
	}

	public List<LoadFieldDefinition> getNonKeyFields() {
		return super.getFields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mohaine.db.engine.ObjectTableMapping#getAllFields()
	 */
	@Override
	public List<LoadFieldDefinition> getFields() {
		List<LoadFieldDefinition> all = new ArrayList<LoadFieldDefinition>();
		all.addAll(keys);
		all.addAll(getNonKeyFields());
		return all;
	}

	public List<FieldDefiniation> getKeys() {
		return keys;
	}

}