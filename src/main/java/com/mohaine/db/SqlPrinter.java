package com.mohaine.db;

import java.sql.SQLException;

public interface SqlPrinter {
    public void outputPreSql(Object sql);

    void outputPostSql(Object sql, long startTime);

    void outputSqlError(Object sql);

    String getBindSqlString(Object o);
}