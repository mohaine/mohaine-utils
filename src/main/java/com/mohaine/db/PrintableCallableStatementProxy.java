package com.mohaine.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class PrintableCallableStatementProxy extends PrintablePreparedStatementProxy implements CallableStatement {

    private CallableStatement callStmt = null;
//    private Map<String, Object> namedBinds;


    //    public void clearBinds() {
//        super.clearBinds();
//        if (namedBinds != null) {
//            namedBinds.clear();
//        }
//    }
//
    void setBind(String name, Object o) {
//        if (namedBinds == null) {
//            namedBinds = new HashMap<String, Object>();
//        }
//        namedBinds.put(name, o);
        System.out.println("*** Named Params are not Implemented ***");
    }

    void setBind(String name, Object o, long length) {
        setBind(name, o);
    }

    void setBind(String name, byte o) {
        setBind(name, new Byte(o));
    }

    void setBind(String name, int o) {
        setBind(name, new Integer(o));
    }

    void setBind(String name, long o) {
        setBind(name, new Long(o));
    }

    void setBind(String name, float o) {
        setBind(name, new Float(o));
    }

    void setBind(String name, double o) {
        setBind(name, new Double(o));
    }


    public PrintableCallableStatementProxy(SqlPrinter printer, CallableStatement callStmt, String sql) {
        super(printer, callStmt, sql);
        this.callStmt = callStmt;
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        setBind(parameterIndex, "?");
        callStmt.registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        setBind(parameterIndex, "?");
        callStmt.registerOutParameter(parameterIndex, sqlType, scale);
    }

    public boolean wasNull() throws SQLException {
        return callStmt.wasNull();
    }

    public String getString(int parameterIndex) throws SQLException {
        return callStmt.getString(parameterIndex);
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        return callStmt.getBoolean(parameterIndex);
    }

    public byte getByte(int parameterIndex) throws SQLException {
        return callStmt.getByte(parameterIndex);
    }

    public short getShort(int parameterIndex) throws SQLException {
        return callStmt.getShort(parameterIndex);
    }

    public int getInt(int parameterIndex) throws SQLException {
        return callStmt.getInt(parameterIndex);
    }

    public long getLong(int parameterIndex) throws SQLException {
        return callStmt.getLong(parameterIndex);
    }

    public float getFloat(int parameterIndex) throws SQLException {
        return callStmt.getFloat(parameterIndex);
    }

    public double getDouble(int parameterIndex) throws SQLException {
        return callStmt.getDouble(parameterIndex);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return callStmt.getBigDecimal(parameterIndex, scale);
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        return callStmt.getBytes(parameterIndex);
    }

    public Date getDate(int parameterIndex) throws SQLException {
        return callStmt.getDate(parameterIndex);
    }

    public Time getTime(int parameterIndex) throws SQLException {
        return callStmt.getTime(parameterIndex);
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return callStmt.getTimestamp(parameterIndex);
    }

    public Object getObject(int parameterIndex) throws SQLException {
        return callStmt.getObject(parameterIndex);
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return callStmt.getBigDecimal(parameterIndex);
    }

    public Ref getRef(int i) throws SQLException {
        return callStmt.getRef(i);
    }

    public Blob getBlob(int i) throws SQLException {
        return callStmt.getBlob(i);
    }

    public Clob getClob(int i) throws SQLException {
        return callStmt.getClob(i);
    }

    public Array getArray(int i) throws SQLException {
        return callStmt.getArray(i);
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return callStmt.getDate(parameterIndex, cal);
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return callStmt.getTime(parameterIndex, cal);
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return callStmt.getTimestamp(parameterIndex, cal);
    }

    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        setBind(paramIndex, "?");
        callStmt.registerOutParameter(paramIndex, sqlType, typeName);
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        setBind(parameterName, "?");
        callStmt.registerOutParameter(parameterName, sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        setBind(parameterName, "?");
        callStmt.registerOutParameter(parameterName, sqlType, scale);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        setBind(parameterName, "?");
        callStmt.registerOutParameter(parameterName, sqlType, typeName);
    }

    public URL getURL(int parameterIndex) throws SQLException {
        return callStmt.getURL(parameterIndex);
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        setBind(parameterName, val);
        callStmt.setURL(parameterName, val);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        setBind(parameterName, null);
        callStmt.setNull(parameterName, sqlType);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        setBind(parameterName, x ? Boolean.TRUE : Boolean.FALSE);
        callStmt.setBoolean(parameterName, x);
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setByte(parameterName, x);
    }

    public void setShort(String parameterName, short x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setShort(parameterName, x);
    }

    public void setInt(String parameterName, int x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setInt(parameterName, x);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setLong(parameterName, x);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setFloat(parameterName, x);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setDouble(parameterName, x);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setBigDecimal(parameterName, x);
    }

    public void setString(String parameterName, String x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setString(parameterName, x);
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setBytes(parameterName, x);
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setDate(parameterName, x);
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setTime(parameterName, x);
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setTimestamp(parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        setBind(parameterName, x, length);
        callStmt.setAsciiStream(parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        setBind(parameterName, x, length);
        callStmt.setAsciiStream(parameterName, x, length);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        setBind(parameterName, x);
        callStmt.setObject(parameterName, x, targetSqlType, scale);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        setBind(parameterName, x);
        callStmt.setObject(parameterName, x, targetSqlType);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setObject(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        setBind(parameterName, reader);
        callStmt.setCharacterStream(parameterName, reader, length);
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        setBind(parameterName, x);
        callStmt.setDate(parameterName, x, cal);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        setBind(parameterName, x);
        callStmt.setTime(parameterName, x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        setBind(parameterName, x);
        callStmt.setTimestamp(parameterName, x, cal);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        setBind(parameterName, null);
        callStmt.setNull(parameterName, sqlType, typeName);
    }

    public String getString(String parameterName) throws SQLException {
        return callStmt.getString(parameterName);
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        return callStmt.getBoolean(parameterName);
    }

    public byte getByte(String parameterName) throws SQLException {
        return callStmt.getByte(parameterName);
    }

    public short getShort(String parameterName) throws SQLException {
        return callStmt.getShort(parameterName);
    }

    public int getInt(String parameterName) throws SQLException {
        return callStmt.getInt(parameterName);
    }

    public long getLong(String parameterName) throws SQLException {
        return callStmt.getLong(parameterName);
    }

    public float getFloat(String parameterName) throws SQLException {
        return callStmt.getFloat(parameterName);
    }

    public double getDouble(String parameterName) throws SQLException {
        return callStmt.getDouble(parameterName);
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        return callStmt.getBytes(parameterName);
    }

    public Date getDate(String parameterName) throws SQLException {
        return callStmt.getDate(parameterName);
    }

    public Time getTime(String parameterName) throws SQLException {
        return callStmt.getTime(parameterName);
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return callStmt.getTimestamp(parameterName);
    }

    public Object getObject(String parameterName) throws SQLException {
        return callStmt.getObject(parameterName);
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return callStmt.getBigDecimal(parameterName);
    }

    public Ref getRef(String parameterName) throws SQLException {
        return callStmt.getRef(parameterName);
    }

    public Blob getBlob(String parameterName) throws SQLException {
        return callStmt.getBlob(parameterName);
    }

    public Clob getClob(String parameterName) throws SQLException {
        return callStmt.getClob(parameterName);
    }

    public Array getArray(String parameterName) throws SQLException {
        return callStmt.getArray(parameterName);
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return callStmt.getDate(parameterName, cal);
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return callStmt.getTime(parameterName, cal);
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return callStmt.getTimestamp(parameterName, cal);
    }

    public URL getURL(String parameterName) throws SQLException {
        return callStmt.getURL(parameterName);
    }

    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        return callStmt.getObject(parameterIndex, map);
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        return callStmt.getObject(parameterName, map);
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return callStmt.getCharacterStream(parameterIndex);
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        return callStmt.getCharacterStream(parameterName);
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return callStmt.getNCharacterStream(parameterIndex);
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return callStmt.getNCharacterStream(parameterName);
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        return callStmt.getNClob(parameterIndex);
    }

    public NClob getNClob(String parameterName) throws SQLException {
        return callStmt.getNClob(parameterName);
    }

    public String getNString(int parameterIndex) throws SQLException {
        return callStmt.getNString(parameterIndex);
    }

    public String getNString(String parameterName) throws SQLException {
        return callStmt.getNString(parameterName);
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        return callStmt.getRowId(parameterIndex);
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return callStmt.getRowId(parameterName);
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return callStmt.getSQLXML(parameterIndex);
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return callStmt.getSQLXML(parameterName);
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        setBind(parameterName, x, length);
        callStmt.setAsciiStream(parameterName, x, length);
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setAsciiStream(parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        setBind(parameterName, x, length);
        callStmt.setBinaryStream(parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setBinaryStream(parameterName, x);
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setBlob(parameterName, x);
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        setBind(parameterName, inputStream, length);
        callStmt.setBlob(parameterName, inputStream, length);
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        setBind(parameterName, inputStream);
        callStmt.setBlob(parameterName, inputStream);
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        setBind(parameterName, reader, length);
        callStmt.setCharacterStream(parameterName, reader, length);
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        setBind(parameterName, reader);
        callStmt.setCharacterStream(parameterName, reader);
    }

    public void setClob(String parameterName, Clob x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setClob(parameterName, x);

    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        setBind(parameterName, reader, length);
        callStmt.setClob(parameterName, reader, length);

    }

    public void setClob(String parameterName, Reader reader) throws SQLException {
        setBind(parameterName, reader);
        callStmt.setClob(parameterName, reader);
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        setBind(parameterName, value, length);
        callStmt.setNCharacterStream(parameterName, value, length);
    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        setBind(parameterName, value);
        callStmt.setNCharacterStream(parameterName, value);
    }

    public void setNClob(String parameterName, NClob value) throws SQLException {
        setBind(parameterName, value);
        callStmt.setNClob(parameterName, value);
    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        setBind(parameterName, reader, length);
        callStmt.setNClob(parameterName, reader, length);
    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {
        setBind(parameterName, reader);
        callStmt.setNClob(parameterName, reader);
    }

    public void setNString(String parameterName, String value) throws SQLException {
        setBind(parameterName, value);
        callStmt.setNString(parameterName, value);
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        setBind(parameterName, x);
        callStmt.setRowId(parameterName, x);
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        setBind(parameterName, xmlObject);
        callStmt.setSQLXML(parameterName, xmlObject);
    }

	@Override
	public <T> T getObject(int parameterIndex, Class<T> c) throws SQLException {
 		return callStmt.getObject(parameterIndex, c);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> c) throws SQLException {
 		return callStmt.getObject(parameterName, c);
	}
    
    	

}