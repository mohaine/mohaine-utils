package com.mohaine.db.orm.engine;

import java.util.ArrayList;
import java.util.List;
 
public class SqlDelete extends Statement {
	private List<Where> wheres = new ArrayList<Where>();

	public List<Where> getWheres() {
		return wheres;
	}

	public void setWheres(List<Where> wheres) {
		this.wheres = wheres;
	}

}