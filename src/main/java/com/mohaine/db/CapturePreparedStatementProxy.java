package com.mohaine.db;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CapturePreparedStatementProxy extends CaptureStatementProxy implements PreparedStatement {

    private PreparedStatement preStmt = null;

    private List<Object> binds;

    private final String sql;

    public CapturePreparedStatementProxy(SqlCapture capture, PreparedStatement preStmt, String sql) {
        super(capture, preStmt, sql);
        this.sql = sql;
        this.preStmt = preStmt;
        capture.preBindSql(sql);
    }

    public void clearBinds() {
        if (binds != null) {
            binds.clear();
        }
    }


    void setBind(int i, Object o) {
        if (binds == null) {
            binds = new ArrayList<Object>();
        }
        while (binds.size() < i) {
            binds.add(null);
        }
        binds.set(i - 1, o);
    }


    @Override
    public String toString() {
        String sql = super.toString();
        return bindSql(sql);
    }

    protected String bindSql(String sql) {
        final boolean hasBinds = binds != null && binds.size() > 0;
        if (!hasBinds) {
            return sql;
        }

        StringBuilder sb = new StringBuilder(sql.length() + 200);
        int bindCount = 0;
        boolean inTic = false;
        final int size = sql.length();
        for (int i = 0; i < size; i++) {
            char ch = sql.charAt(i);
            if (ch == '\'') {
                inTic = !inTic;
                sb.append(ch);
            } else if (!inTic && hasBinds && ch == '?') {
                bindCount = appendBindByIndex(sb, bindCount);
            } else if (!inTic && ch == ':') {
                StringBuilder nameSb = new StringBuilder();
                for (; i + 1 < size; i++) {
                    char chSub = sql.charAt(i + 1);
                    if (Character.isLetterOrDigit(chSub) || chSub == '_') {
                        nameSb.append(chSub);
                    } else {
                        break;
                    }
                }
                bindCount = appendBindByIndex(sb, bindCount);

            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private int appendBindByIndex(StringBuilder sb, int bindCount) {
        if (binds.size() > bindCount) {
            Object o = binds.get(bindCount);
            if (o != null) {
                String objectStringValue = getBindSqlString(o);
                sb.append(objectStringValue);
            } else {
                sb.append("NULL");
            }
        }
        bindCount++;
        return bindCount;
    }

    private String getBindSqlString(Object o) {
        return capture.getBindSqlString(o);
    }

    private String getReaderValue(Reader reader, int length) {
        return getReaderValue(reader, (long) length);
    }

    private String getReaderValue(Reader reader) {
        return getReaderValue(reader, (long) -1);
    }

    private String getReaderValue(Reader reader, long length) {

        if (length < 0) {
            length = Long.MAX_VALUE;
        }
        StringBuilder fullBuffer = new StringBuilder();
        try {
            char[] readBuffer = new char[1000];
            while (true) {
                int maxReadSize = (int) Math.min(readBuffer.length, length - fullBuffer.length());
                if (maxReadSize < 1) {
                    break;
                }

                int readSize = reader.read(readBuffer, 0, maxReadSize);
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    fullBuffer.append(readBuffer, 0, readSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String stringValue = fullBuffer.toString();
        return stringValue;
    }

    private byte[] getStreamValue(InputStream inputStream, int length) {
        return getStreamValue(inputStream, (long) length);
    }

    private byte[] getStreamValue(InputStream inputStream) {
        return getStreamValue(inputStream, (long) -1);
    }

    private byte[] getStreamValue(InputStream inputStream, long length) {

        if (length < 0) {
            length = Long.MAX_VALUE;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            byte[] readBuffer = new byte[1000];
            while (true) {
                int maxReadSize = (int) Math.min(readBuffer.length, length - out.size());
                if (maxReadSize < 1) {
                    break;
                }

                int readSize = inputStream.read(readBuffer, 0, maxReadSize);
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    out.write(readBuffer, 0, readSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return preStmt.getMetaData();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setBind(parameterIndex, null);
        preStmt.setNull(parameterIndex, sqlType);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setBind(parameterIndex, new Boolean(x));
        preStmt.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        setBind(parameterIndex, new Byte(x));
        preStmt.setByte(parameterIndex, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        setBind(parameterIndex, new Short(x));
        preStmt.setShort(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        setBind(parameterIndex, new Integer(x));
        preStmt.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        setBind(parameterIndex, new Long(x));
        preStmt.setLong(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        setBind(parameterIndex, new Float(x));
        preStmt.setFloat(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        setBind(parameterIndex, new Double(x));
        preStmt.setDouble(parameterIndex, x);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setBigDecimal(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setString(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setBytes(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setDate(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setTime(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setTimestamp(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        byte[] bytes = getStreamValue(x, length);
        setBind(parameterIndex, new String(bytes));
        preStmt.setAsciiStream(parameterIndex, new ByteArrayInputStream(bytes), length);
    }

    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        byte[] bytes = getStreamValue(x, length);
        setBind(parameterIndex, new String(bytes));
        preStmt.setUnicodeStream(parameterIndex, new ByteArrayInputStream(bytes), length);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        byte[] bytes = getStreamValue(x, length);
        setBind(parameterIndex, new String(bytes));
        preStmt.setBinaryStream(parameterIndex, new ByteArrayInputStream(bytes), length);
    }

    public void clearParameters() throws SQLException {
        clearBinds();
        preStmt.clearParameters();
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setObject(parameterIndex, x);
    }

    public void addBatch() throws SQLException {
        addBatchedSql(bindSql(sql));
        preStmt.addBatch();
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        String stringValue = getReaderValue(reader, length);
        setBind(parameterIndex, stringValue);
        preStmt.setCharacterStream(parameterIndex, new StringReader(stringValue), length);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setDate(parameterIndex, x, cal);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setTime(parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setTimestamp(parameterIndex, x, cal);
    }

    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        setBind(paramIndex, null);
        preStmt.setNull(paramIndex, sqlType, typeName);
    }

    public void setArray(int i, Array x) throws SQLException {
        setBind(i, x);
        preStmt.setArray(i, x);
    }

    public void setBlob(int i, Blob x) throws SQLException {
        setBind(i, x);
        preStmt.setBlob(i, x);
    }

    public void setClob(int i, Clob x) throws SQLException {
        setBind(i, x);
        preStmt.setClob(i, x);
    }

    public void setRef(int i, Ref x) throws SQLException {
        setBind(i, x);
        preStmt.setRef(i, x);
    }

    @Override
    public void clearBatchedSql() {
        this.clearBinds();
        super.clearBatchedSql();
    }


    public int executeUpdate() throws SQLException {
        printPreExecute();
        boolean ok = false;
        long startTime = System.currentTimeMillis();
        Integer results = null;
        try {
            results = preStmt.executeUpdate();
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

    public boolean execute() throws SQLException {
        printPreExecute();
        boolean ok = false;
        long startTime = System.currentTimeMillis();
        Boolean results = null;
        try {
            results = preStmt.execute();
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

    public ResultSet executeQuery() throws SQLException {
        printPreExecute();
        long startTime = System.currentTimeMillis();
        boolean ok = false;

        CaptureResultSet captureResultSet = null;
        try {
            ResultSet results = preStmt.executeQuery();
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

    public void setURL(int parameterIndex, URL x) throws SQLException {
        setBind(parameterIndex, x);
        preStmt.setURL(parameterIndex, x);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return preStmt.getParameterMetaData();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        byte[] bytes = getStreamValue(x, length);
        setBind(parameterIndex, new String(bytes));
        preStmt.setAsciiStream(parameterIndex, new ByteArrayInputStream(bytes), length);
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        byte[] bytes = getStreamValue(x);
        setBind(parameterIndex, new String(bytes));
        preStmt.setAsciiStream(parameterIndex, new ByteArrayInputStream(bytes));
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        byte[] bytes = getStreamValue(x, length);
        setBind(parameterIndex, new String(bytes));
        preStmt.setBinaryStream(parameterIndex, new ByteArrayInputStream(bytes), length);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        byte[] bytes = getStreamValue(x);
        setBind(parameterIndex, new String(bytes));
        preStmt.setBinaryStream(parameterIndex, new ByteArrayInputStream(bytes));
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        byte[] bytes = getStreamValue(inputStream, length);
        setBind(parameterIndex, new String(bytes));
        preStmt.setBlob(parameterIndex, new ByteArrayInputStream(bytes), length);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        byte[] bytes = getStreamValue(inputStream);
        setBind(parameterIndex, new String(bytes));
        preStmt.setBlob(parameterIndex, new ByteArrayInputStream(bytes));

    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        String stringValue = getReaderValue(reader, length);
        setBind(parameterIndex, stringValue);
        preStmt.setCharacterStream(parameterIndex, new StringReader(stringValue), length);
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        String stringValue = getReaderValue(reader);
        setBind(parameterIndex, stringValue);
        preStmt.setCharacterStream(parameterIndex, new StringReader(stringValue));
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        String stringValue = getReaderValue(reader, length);
        setBind(parameterIndex, stringValue);
        preStmt.setClob(parameterIndex, new StringReader(stringValue), length);

    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        String stringValue = getReaderValue(reader);
        setBind(parameterIndex, stringValue);
        preStmt.setClob(parameterIndex, new StringReader(stringValue));
    }

    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        String stringValue = getReaderValue(reader, length);
        setBind(parameterIndex, stringValue);
        preStmt.setNCharacterStream(parameterIndex, new StringReader(stringValue), length);
    }

    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        String stringValue = getReaderValue(reader);
        setBind(parameterIndex, stringValue);
        preStmt.setNCharacterStream(parameterIndex, new StringReader(stringValue));

    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException { // TODO
        // setBind(parameterIndex,
        // "NClob");
        preStmt.setNClob(parameterIndex, value);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        String stringValue = getReaderValue(reader, length);
        setBind(parameterIndex, stringValue);
        preStmt.setNClob(parameterIndex, new StringReader(stringValue), length);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        String stringValue = getReaderValue(reader);
        setBind(parameterIndex, stringValue);
        preStmt.setNClob(parameterIndex, new StringReader(stringValue));
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        setBind(parameterIndex, value);
        preStmt.setNString(parameterIndex, value);

    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        preStmt.setRowId(parameterIndex, x);

    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        preStmt.setSQLXML(parameterIndex, xmlObject);
    }

}
