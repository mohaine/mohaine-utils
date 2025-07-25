package com.mohaine.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class CaptureResultSet extends ProxyResultSet {
    protected final SqlDataCapture capture;
    private final Object sql;
    private final ResultSet rs;
    private final long startTime;
    private SqlDataCapture.QueryResults results = null;

    public CaptureResultSet(SqlDataCapture capture, Object sql, long startTime, ResultSet rs) {
        super(rs);
        this.capture = capture;
        this.sql = sql;
        this.rs = rs;
        this.startTime = startTime;
    }

    @Override
    public boolean next() throws SQLException {
        boolean next = super.next();

        if (results == null) {
            ResultSetMetaData metaData = rs.getMetaData();
            var columnCount = metaData.getColumnCount();
            var columnNames = new ArrayList<String>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                columnNames.add(metaData.getColumnName(i + 1));
            }
            results = new SqlDataCapture.QueryResults(columnNames);
        }

        if (next) {
            int columnCount = results.getColumns().size();
            var rowData = new ArrayList<Object>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                rowData.add(rs.getObject(i + 1));
            }
            results.getRows().add(rowData);
        }

        return next;
    }

    @Override
    public void close() throws SQLException {
        capture.onComplete(sql.toString(), startTime, results);
        super.close();
    }
}
