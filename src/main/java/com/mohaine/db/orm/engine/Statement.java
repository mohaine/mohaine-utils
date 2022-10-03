package com.mohaine.db.orm.engine;

import java.util.ArrayList;
import java.util.List;

class Statement {

    private List<SqlTable> tables = new ArrayList<SqlTable>();

    public Statement() {
    }

    public List<SqlTable> getTables() {
        return tables;
    }

    public void setTables(List<SqlTable> tables) {
        this.tables = tables;
    }

}
