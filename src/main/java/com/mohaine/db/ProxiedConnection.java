/*
 * Created on Aug 5, 2005
 *
 */
package com.mohaine.db;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ProxiedConnection implements Connection {

    private Connection proxiedConnection = null;

    public ProxiedConnection(Connection proxiedConnection) {
        this.proxiedConnection = proxiedConnection;
    }

    public Connection getProxiedConnection() {
        return proxiedConnection;
    }

    public int getTransactionIsolation() throws SQLException {
        return proxiedConnection.getTransactionIsolation();
    }

    public void clearWarnings() throws SQLException {
        proxiedConnection.clearWarnings();

    }

    public void close() throws SQLException {
        proxiedConnection.close();
    }

    public void commit() throws SQLException {
        proxiedConnection.commit();
    }

    public void rollback() throws SQLException {
        proxiedConnection.rollback();
    }

    public boolean getAutoCommit() throws SQLException {
        return proxiedConnection.getAutoCommit();
    }

    public boolean isClosed() throws SQLException {
        return proxiedConnection.isClosed();
    }

    public boolean isReadOnly() throws SQLException {
        return proxiedConnection.isReadOnly();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        proxiedConnection.setTransactionIsolation(level);

    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        proxiedConnection.setAutoCommit(autoCommit);

    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        proxiedConnection.setReadOnly(readOnly);
    }

    public String getCatalog() throws SQLException {
        return proxiedConnection.getCatalog();
    }

    public void setCatalog(String catalog) throws SQLException {
        proxiedConnection.setCatalog(catalog);
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return proxiedConnection.getMetaData();
    }

    public SQLWarning getWarnings() throws SQLException {
        return proxiedConnection.getWarnings();
    }

    public String nativeSQL(String sql) throws SQLException {
        return proxiedConnection.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return proxiedConnection.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return proxiedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return proxiedConnection.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return proxiedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return proxiedConnection.getTypeMap();
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return proxiedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return proxiedConnection.prepareStatement(sql, columnIndexes);
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return proxiedConnection.setSavepoint(name);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return proxiedConnection.prepareStatement(sql, columnNames);
    }

    public void setHoldability(int holdability) throws SQLException {
        proxiedConnection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return proxiedConnection.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return proxiedConnection.setSavepoint();
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        proxiedConnection.releaseSavepoint(savepoint);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        proxiedConnection.rollback(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return proxiedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return proxiedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return proxiedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    public Statement createStatement() throws SQLException {
        return proxiedConnection.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return proxiedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        proxiedConnection.setTypeMap(map);
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return proxiedConnection.createArrayOf(typeName, elements);
    }

    public Blob createBlob() throws SQLException {
        return proxiedConnection.createBlob();
    }

    public Clob createClob() throws SQLException {
        return proxiedConnection.createClob();
    }

    public NClob createNClob() throws SQLException {
        return proxiedConnection.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return proxiedConnection.createSQLXML();
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return proxiedConnection.createStruct(typeName, attributes);
    }

    public Properties getClientInfo() throws SQLException {
        return proxiedConnection.getClientInfo();
    }

    public String getClientInfo(String name) throws SQLException {
        return proxiedConnection.getClientInfo(name);
    }

    public boolean isValid(int timeout) throws SQLException {
        return proxiedConnection.isValid(timeout);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        proxiedConnection.setClientInfo(properties);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        proxiedConnection.setClientInfo(name, value);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return proxiedConnection.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return proxiedConnection.unwrap(iface);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        proxiedConnection.abort(executor);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return proxiedConnection.getNetworkTimeout();
    }

    @Override
    public String getSchema() throws SQLException {
        return proxiedConnection.getSchema();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        proxiedConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        proxiedConnection.setSchema(schema);
    }

}
