package com.mohaine.db;


public class SqlCaptureConsole extends SqlCaptureAbstract {

    @Override
    public void beforeRun(Object sql) {
    }


    @Override
    public void afterRun(Object sql, long startTime) {
        try {
            long runTime = System.currentTimeMillis() - startTime;
            logMessage(String.format("%6d:%s", runTime, sql.toString()));
        } catch (Throwable t) {
            logMessage("Error during SQL log" + t.getMessage());
        }
    }

    @Override
    public void afterError(Object sql) {
        try {
            logMessage("ERROR: " + sql.toString());
        } catch (Throwable t) {
            logMessage("Error during SQL log" + t.getMessage());
        }
    }

    public void logMessage(Object sql) {
        System.out.println(sql);
    }

}
