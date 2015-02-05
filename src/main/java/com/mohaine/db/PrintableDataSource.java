package com.mohaine.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class PrintableDataSource implements DataSource {

    private final DataSource proxiedDataSource;
    private final SqlPrinter printer;

    public PrintableDataSource(DataSource proxiedDataSource, SqlPrinter printer) {
        this.proxiedDataSource = proxiedDataSource;
        this.printer = printer;
    }

    public Connection getConnection() throws SQLException {
        return new PrintableConnectionProxy(proxiedDataSource.getConnection(),  printer);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        final Connection connection = proxiedDataSource.getConnection(username, password);
        return new PrintableConnectionProxy(connection,  printer);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return proxiedDataSource.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        proxiedDataSource.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        proxiedDataSource.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return proxiedDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return proxiedDataSource.getParentLogger();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return proxiedDataSource.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return proxiedDataSource.isWrapperFor(iface);
    }
}