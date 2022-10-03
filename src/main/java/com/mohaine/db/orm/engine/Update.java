package com.mohaine.db.orm.engine;

import java.util.ArrayList;
import java.util.List;

public class Update extends Statement {
    private List<SqlSet> sets = new ArrayList<SqlSet>();
    private List<Where> wheres = new ArrayList<Where>();

    public List<SqlSet> getSets() {
        return sets;
    }

    public void setSets(List<SqlSet> sets) {
        this.sets = sets;
    }

    public List<Where> getWheres() {
        return wheres;
    }

    public void setWheres(List<Where> wheres) {
        this.wheres = wheres;
    }

}
