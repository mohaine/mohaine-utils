package com.mohaine.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CaptureStatementProxy implements Statement {
    private List<String> batchedSql = null;
    private Statement stmt = null;

    private String sql;
    protected final SqlCapture capture;

    public CaptureStatementProxy(SqlCapture printer, Statement stmt) {
        this.capture = printer;
        this.stmt = stmt;
        clearBatchedSql();
    }

    public CaptureStatementProxy(SqlCapture printer, Statement stmt,
                                 String sql) {
        this(printer, stmt);
        this.sql = sql;
    }

    protected void addBatchedSql(String sql) {
        if (batchedSql == null) {
            batchedSql = new ArrayList<String>();
        }
        batchedSql.add(sql);
    }

    protected void clearBatchedSql() {
        if (batchedSql == null) {
            batchedSql = new ArrayList<String>();
        }
        batchedSql.clear();
    }

    protected void printPreExecute() {
        capture.beforeRun(this);
    }

    protected void printPostExecute(long startTime) {
        capture.afterRun(this, startTime);
    }

    protected void printError() {
        capture.afterError(this);
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (sql != null && sql.length() > 0) {
            sb.append(SqlCleaner.cleanSql(sql));
        }
        if (batchedSql != null) {
            for (int i = 0, size = batchedSql.size(); i < size; i++) {
                if (sb.length() > 0) {
                    sb.append(';');
                }
                sb.append(SqlCleaner.cleanSql((String) batchedSql.get(i)));
            }
        }
        return sb.toString();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        this.sql = sql;
        printPreExecute();
        boolean ok = false;
        long startTime = System.currentTimeMillis();

        CaptureResultSet captureResultSet = null;
        try {
            ResultSet results = stmt.executeQuery(sql);
            if (capture instanceof SqlDataCapture) {
                captureResultSet = new CaptureResultSet((SqlDataCapture) capture, this, startTime, results);
                results = captureResultSet;
            }
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        this.sql = sql;
        printPreExecute();
        boolean ok = false;
        long startTime = System.currentTimeMillis();
        Integer results = null;
        try {
            results = stmt.executeUpdate(sql);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
            if (capture instanceof SqlDataCapture) {
                ((SqlDataCapture) capture).onComplete(this, startTime, new SqlDataCapture.QueryResults(results));
            }
        }
    }

    public void close() throws SQLException {
        stmt.close();
    }

    public int getMaxFieldSize() throws SQLException {
        return stmt.getMaxFieldSize();
    }

    public void setMaxFieldSize(int max) throws SQLException {
    }

    public int getMaxRows() throws SQLException {
        return stmt.getMaxRows();
    }

    public void setMaxRows(int max) throws SQLException {
        stmt.setMaxRows(max);
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        stmt.setEscapeProcessing(enable);
    }

    public int getQueryTimeout() throws SQLException {
        return stmt.getQueryTimeout();
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        stmt.setQueryTimeout(seconds);
    }

    public void cancel() throws SQLException {
        stmt.cancel();
    }

    public SQLWarning getWarnings() throws SQLException {
        return stmt.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        stmt.clearWarnings();
    }

    public void setCursorName(String name) throws SQLException {
        stmt.setCursorName(name);
    }

    public boolean execute(String sql) throws SQLException {
        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        Boolean results = null;
        try {
            results = stmt.execute(sql);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
            if (capture instanceof SqlDataCapture) {
                ((SqlDataCapture) capture).onComplete(this, startTime, new SqlDataCapture.QueryResults(results));
            }
        }
    }

    public ResultSet getResultSet() throws SQLException {
        return stmt.getResultSet();
    }

    public int getUpdateCount() throws SQLException {
        return stmt.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException {
        return stmt.getMoreResults();
    }

    public void setFetchDirection(int direction) throws SQLException {
        stmt.setFetchDirection(direction);
    }

    public int getFetchDirection() throws SQLException {
        return stmt.getFetchDirection();
    }

    public void setFetchSize(int rows) throws SQLException {
        stmt.setFetchSize(rows);
    }

    public int getFetchSize() throws SQLException {
        return stmt.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return stmt.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException {
        return stmt.getResultSetType();
    }

    public void addBatch(String sql) throws SQLException {
        addBatchedSql(sql);
        stmt.addBatch(sql);
    }

    public void clearBatch() throws SQLException {
        clearBatchedSql();
        stmt.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        this.sql = null;
        printPreExecute();
        boolean ok = false;
        long startTime = System.currentTimeMillis();
        int[] results = null;
        try {
             results = stmt.executeBatch();
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
            if (capture instanceof SqlDataCapture) {
                ((SqlDataCapture) capture).onComplete(this, startTime, new SqlDataCapture.QueryResults(results));
            }
            clearBatchedSql();
        }
    }

    public Connection getConnection() throws SQLException {
        return stmt.getConnection();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return stmt.getMoreResults(current);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return stmt.getGeneratedKeys();
    }

    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {

        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        try {
            int results = stmt.executeUpdate(sql, autoGeneratedKeys);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        try {
            int results = stmt.executeUpdate(sql, columnIndexes);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        try {
            int results = stmt.executeUpdate(sql, columnNames);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException {
        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        try {
            boolean results = stmt.execute(sql, autoGeneratedKeys);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        try {
            boolean results = stmt.execute(sql, columnIndexes);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        this.sql = sql;
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;
        try {
            boolean results = stmt.execute(sql, columnNames);
            ok = true;
            return results;
        } finally {
            if (ok) {
                printPostExecute(startTime);
            } else {
                printError();
            }
        }
    }

    public int getResultSetHoldability() throws SQLException {
        return stmt.getResultSetHoldability();
    }

    public boolean isClosed() throws SQLException {
        return stmt.isClosed();
    }

    public boolean isPoolable() throws SQLException {
        return stmt.isPoolable();
    }

    public void setPoolable(boolean arg0) throws SQLException {
        stmt.setPoolable(arg0);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return stmt.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return stmt.unwrap(iface);
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        stmt.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return stmt.isCloseOnCompletion();
    }

}
