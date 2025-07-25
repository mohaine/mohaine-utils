package com.mohaine.db;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class CaptureDataSource implements DataSource {

    private final DataSource proxiedDataSource;
    private final SqlCapture capture;

    public CaptureDataSource(DataSource proxiedDataSource, SqlCapture capture) {
        this.proxiedDataSource = proxiedDataSource;
        this.capture = capture;
    }

    public Connection getConnection() throws SQLException {
        return new CaptureConnectionProxy(proxiedDataSource.getConnection(), capture);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        final Connection connection = proxiedDataSource.getConnection(username, password);
        return new CaptureConnectionProxy(connection, capture);
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
