package com.mohaine.db;


public interface SqlCapture {
    public void beforeRun(Object sql);

    public void afterRun(Object sql, long startTime);

    public void afterError(Object sql);

    public String getBindSqlString(Object o);

    public void preBindSql(String sql);
}
