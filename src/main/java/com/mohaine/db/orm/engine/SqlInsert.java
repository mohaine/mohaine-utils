package com.mohaine.db.orm.engine;

import java.util.ArrayList;
import java.util.List;

public class SqlInsert extends Statement {
    private List<SqlSet> values = new ArrayList<SqlSet>();

    /**
     * @return List
     */
    public List<SqlSet> getValues() {
        return values;
    }

    /**
     * Sets the sets.
     *
     * @param sets The sets to set
     */
    public void setValues(List<SqlSet> sets) {
        this.values = sets;
    }
}
