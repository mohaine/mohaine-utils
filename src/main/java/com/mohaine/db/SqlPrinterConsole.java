package com.mohaine.db;


public class SqlPrinterConsole extends SqlPrinterAbstract {

    @Override
    public void outputPreSql(Object sql) {
    }


    @Override
    public void outputPostSql(Object sql, long startTime) {
        try {
            long runTime = System.currentTimeMillis() - startTime;
            logMessage(String.format("%6d:%s", runTime, sql.toString()));
        } catch (Throwable t) {
            logMessage("Error during SQL log" + t.getMessage());
        }
    }

    @Override
    public void outputSqlError(Object sql) {
        try {
            logMessage("ERROR: " + sql.toString());
        } catch (Throwable t) {
            logMessage("Error during SQL log" + t.getMessage());
        }
    }

    protected void logMessage(Object sql) {
        System.out.println(sql);
    }

}