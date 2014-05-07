package com.mohaine.db;

import com.mohaine.db.orm.engine.SqlGenerator;
import com.mohaine.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseUtils {
    public static boolean execute(Connection conn, String sql)
        throws SQLException {
        return execute(conn, sql, null);
    }

    public static int update(Connection conn, String sql, Object[] binds)
        throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            if (binds != null && binds.length > 0) {
                for (int i = 0; i < binds.length; i++) {
                    Object object = binds[i];
                    setObject(ps, i + 1, object);
                }
            }
            return ps.executeUpdate();
        } finally {
            DatabaseUtils.close(ps);
        }

    }

    public static boolean execute(Connection conn, String sql, Object[] binds)
        throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            if (binds != null && binds.length > 0) {
                for (int i = 0; i < binds.length; i++) {
                    Object object = binds[i];
                    setObject(ps, i + 1, object);
                }
            }
            return ps.execute();
        } finally {
            DatabaseUtils.close(ps);
        }
    }

    public static Object getObject(ResultSet resultSet, int index)
        throws SQLException {
        return getObject(resultSet, index, resultSet.getMetaData()
            .getColumnType(index));
    }

    public static Object getObject(ResultSet resultSet, int index, int type)
        throws SQLException {
        Object value = null;
        if (type == Types.VARBINARY) {
            value = resultSet.getString(index);
        } else if (type == Types.CLOB) {
            Clob clob = resultSet.getClob(index);
            if (clob != null) {
                value = DatabaseUtils.readClob(clob);
            }
        } else if (type == Types.BLOB) {
            Blob blob = resultSet.getBlob(index);
            if (blob != null) {
                value = DatabaseUtils.readBlobAsString(blob);
            }
        } else if (type == Types.TIME) {
            value = resultSet.getTimestamp(index);
        } else if (type == Types.TIMESTAMP) {
            value = resultSet.getTimestamp(index);
        } else if (type == Types.DATE) {
            value = resultSet.getTimestamp(index);
        } else {
            value = resultSet.getObject(index);
        }
        return value;
    }

    private static void setObject(PreparedStatement statement, int i,
                                  Object value) throws SQLException {
        Object bindObject = SqlGenerator.getBindObject(value);
        if (bindObject == null) {
            statement.setNull(i, Types.CHAR);
        } else {
            statement.setObject(i, bindObject);
        }
    }

    public static Object selectSingle(Connection conn, String sql)
        throws SQLException {
        return selectSingle(conn, sql, null);
    }

    public static Object[][] select(Connection conn, String sql)
        throws SQLException {
        return select(conn, sql, null);
    }

    public static Object[][] select(Connection conn, String sql, Object[] binds)
        throws SQLException {
        return select(conn, sql, binds, -1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object[][] select(Connection conn, String sql,
                                    Object[] binds, int maxCount) throws SQLException {
        List results1 = new ArrayList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            if (binds != null && binds.length > 0) {
                for (int i = 0; i < binds.length; i++) {
                    Object object = binds[i];
                    setObject(ps, i + 1, object);
                }
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                int columnCount = rs.getMetaData().getColumnCount();
                Object[] rowResults = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowResults[i] = getObject(rs, i + 1);
                }
                results1.add(rowResults);

                if (maxCount > 0 && results1.size() == maxCount) {
                    break;
                }
            }
        } finally {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(ps);
        }
        List results = results1;
        return (Object[][]) results.toArray(new Object[results.size()][]);
    }

    public static Object selectSingle(Connection conn, String sql,
                                      Object[] binds) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            if (binds != null && binds.length > 0) {
                for (int i = 0; i < binds.length; i++) {
                    Object object = binds[i];
                    setObject(ps, i + 1, object);
                }
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return getObject(rs, 1);
            }

        } finally {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(ps);
        }
        return null;
    }

    public static Object selectSingle(Connection conn, List<?> binds, String sql)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            if (binds != null && binds.size() > 0) {
                for (int i = 0, size = binds.size(); i < size; i++) {
                    Object object = binds.get(i);
                    setObject(ps, i + 1, object);
                }
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return getObject(rs, 1);
            }

        } finally {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(ps);
        }
        return null;
    }

    public static String selectCsv(Connection conn, String sql, Object[] binds)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        try {
            ps = conn.prepareStatement(sql);

            if (binds != null && binds.length > 0) {
                for (int i = 0; i < binds.length; i++) {
                    Object object = binds[i];
                    setObject(ps, i + 1, object);
                }
            }

            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                if (sb.length() > 0) {
                    sb.append("\r\n");
                }
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        sb.append(',');
                    }
                    sb.append(rs.getString(i));
                }
            }

        } finally {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(ps);
        }
        return sb.toString();
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // Ignore
            }
        }

    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // Ignore
            }
        }

    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Ignore
            }
        }

    }

    public static String readClob(Clob value) throws SQLException {
        InputStream is = value.getAsciiStream();
        try {
            return new String(StreamUtils.readStream(is));
        } catch (IOException e) {
            throw new SQLException("Failed to read Clob");
        }
    }

    public static String readBlobAsString(Blob value) throws SQLException {
        InputStream is = value.getBinaryStream();
        try {
            return new String(StreamUtils.readStream(is));
        } catch (IOException e) {
            throw new SQLException("Failed to read Blob");
        }
    }


    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                // ignore
            }
        }
    }


}