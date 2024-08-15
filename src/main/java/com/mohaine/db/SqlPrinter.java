package com.mohaine.db;


public interface SqlPrinter {
    public void outputPreSql(Object sql);

    public void outputPostSql(Object sql, long startTime);

    public void outputSqlError(Object sql);

    public String getBindSqlString(Object o);

    public void outputPreBindSql(String sql);
}
