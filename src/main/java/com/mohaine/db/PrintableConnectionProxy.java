package com.mohaine.db;

import java.sql.*;


public class PrintableConnectionProxy extends ProxiedConnection {

    private final SqlPrinter printer;

    public PrintableConnectionProxy(Connection proxiedConnection, SqlPrinter printer) {
        super(proxiedConnection);
        this.printer = printer;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new PrintableStatementProxy(printer, super.createStatement());
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new PrintableStatementProxy(printer, super.createStatement(resultSetType, resultSetConcurrency));
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return new PrintableCallableStatementProxy(printer, super.prepareCall(sql), sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new PrintableCallableStatementProxy(printer, super.prepareCall(sql, resultSetType, resultSetConcurrency),
                sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new PrintablePreparedStatementProxy(printer, super.prepareStatement(sql), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws
            SQLException {
        return new PrintablePreparedStatementProxy(printer,
                super.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return new PrintablePreparedStatementProxy(printer,
                super.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new PrintablePreparedStatementProxy(printer, super.prepareStatement(sql, columnIndexes), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new PrintablePreparedStatementProxy(printer, super.prepareStatement(sql, columnNames), sql);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws
            SQLException {
        return new PrintableStatementProxy(printer,
                super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return new PrintableCallableStatementProxy(printer,
                super.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new PrintablePreparedStatementProxy(printer, super.prepareStatement(sql, autoGeneratedKeys), sql);
    }

}
