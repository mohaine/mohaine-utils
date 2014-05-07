package com.mohaine.db;

import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

class SqlPrinterLogger extends SqlPrinterAbstract {
    public static final Level DEFAULT_POST_PRINT_PRIORITY = Level.FINEST;

    private Logger objectLogger = null;

    @Override
    public void outputPreSql(Object sql) {
    }

    @Override
    public void outputPostSql(Object sql, long startTime) {
        try {
            Logger logger = getLogger();
            long runTime = System.currentTimeMillis() - startTime;
            if (runTime > 5000) {
                logger.log(Level.WARNING, "LONG RUNNING SQL: " + runTime + " - " + sql.toString());
            } else {
                if (logger.isLoggable(DEFAULT_POST_PRINT_PRIORITY)) {
                    logger.log(DEFAULT_POST_PRINT_PRIORITY, runTime + " - " + sql.toString());
                }
            }
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Error during SQL log", t);
        }
    }

    @Override
    public void outputSqlError(Object sql) {
        try {
            getLogger().log(Level.SEVERE, sql.toString());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, sql.toString());
        }
    }

    protected Logger getLogger() {
        if (objectLogger == null) {
            objectLogger = Logger.getLogger(getClass().getSimpleName());
        }
        return objectLogger;
    }
}