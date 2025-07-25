package com.mohaine.db;

import org.junit.Test;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CaptureConnection_UT {

    @Test
    public void testCapturesSqlThatWasRan() throws Exception {


        Class.forName("org.h2.Driver");

        try (var conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1")) {
            DatabaseUtils.execute(conn, "create table test (id INTEGER, text_column varchar(20) default 'abc',PRIMARY KEY (id))");
            DatabaseUtils.execute(conn, "insert into test (id , text_column) values (1,'1 Some test data')");
            DatabaseUtils.execute(conn, "insert into test (id , text_column) values (2,'2 Some test data')");


            MySqlCaptureAbstract capture = new MySqlCaptureAbstract();
            var captureConn = new CaptureConnectionProxy(conn, capture);

            // createStatement works
            try (var stmt = captureConn.createStatement()) {
                stmt.execute("SELECT * FROM test");
                stmt.executeUpdate("update test set text_column = '234' where id = 3;");
                try (var rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1")) {
                    while (rs.next()) {
                        assertEquals("1 Some test data", rs.getString("text_column"));
                    }
                }
            }

            var ranSql = capture.ranSql;
            var results = capture.results;
            assertEquals(3, ranSql.size());
            assertEquals("SELECT * FROM test", ranSql.get(0));
            assertEquals("update test set text_column = '234' where id = 3;", ranSql.get(1));
            assertEquals("SELECT * FROM test WHERE id = 1", ranSql.get(2));

            assertEquals(Arrays.asList(true), results.get("SELECT * FROM test").getRows().get(0));
            assertEquals(Arrays.asList(0), results.get("update test set text_column = '234' where id = 3;").getRows().get(0));

            assertEquals(Arrays.asList("ID", "TEXT_COLUMN"), results.get("SELECT * FROM test WHERE id = 1").getColumns());
            assertEquals(Arrays.asList(1, "1 Some test data"), results.get("SELECT * FROM test WHERE id = 1").getRows().get(0));

            ranSql.clear();
            results.clear();

            // prepareStatement works
            try (var stmt = captureConn.prepareStatement("SELECT * FROM test WHERE id = ?")) {
                stmt.setInt(1, 2);
                try (var rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        assertEquals("2 Some test data", rs.getString("text_column"));
                    }
                }

                stmt.setInt(1, 3);
                stmt.execute();
            }

            assertEquals(Arrays.asList("ID", "TEXT_COLUMN"), results.get("SELECT * FROM test WHERE id = 2").getColumns());
            assertEquals(Arrays.asList(2, "2 Some test data"), results.get("SELECT * FROM test WHERE id = 2").getRows().get(0));
            assertEquals(Arrays.asList(true), results.get("SELECT * FROM test WHERE id = 3").getRows().get(0));
            ranSql.clear();
            results.clear();

            try (var stmt = captureConn.prepareStatement("update test set text_column = ? where id = ?")) {
                stmt.setString(1, "abc");
                stmt.setInt(2, 3);
                stmt.executeUpdate();

                stmt.setString(1, "abc");
                stmt.setInt(2, 1);
                stmt.executeUpdate();

                stmt.setString(1, "111");
                stmt.setInt(2, 1);
                stmt.addBatch();
                stmt.setString(1, "333");
                stmt.setInt(2, 3);
                stmt.addBatch();

                stmt.executeBatch();
            }


            assertEquals("update test set text_column = '111' where id = 1;update test set text_column = '333' where id = 3", ranSql.get(2));
            assertEquals(Arrays.asList(0), results.get("update test set text_column = 'abc' where id = 3").getRows().get(0));
            assertEquals(Arrays.asList(1), results.get("update test set text_column = 'abc' where id = 1").getRows().get(0));
            assertEquals(Arrays.asList(1), results.get("update test set text_column = '111' where id = 1;update test set text_column = '333' where id = 3").getRows().get(0));
            assertEquals(Arrays.asList(0), results.get("update test set text_column = '111' where id = 1;update test set text_column = '333' where id = 3").getRows().get(1));

            ranSql.clear();
            results.clear();

            try (var stmt = captureConn.prepareCall("update test set text_column = ? where id = ?")) {
                stmt.setString(1, "abc");
                stmt.setInt(2, 3);
                stmt.executeUpdate();

                stmt.setString(1, "abc");
                stmt.setInt(2, 1);
                stmt.executeUpdate();

                stmt.setString(1, "111");
                stmt.setInt(2, 1);
                stmt.addBatch();
                stmt.setString(1, "333");
                stmt.setInt(2, 3);
                stmt.addBatch();

                stmt.executeBatch();
            }

            assertEquals("update test set text_column = '111' where id = 1;update test set text_column = '333' where id = 3", ranSql.get(2));
            assertEquals(Arrays.asList(0), results.get("update test set text_column = 'abc' where id = 3").getRows().get(0));
            assertEquals(Arrays.asList(1), results.get("update test set text_column = 'abc' where id = 1").getRows().get(0));
            assertEquals(Arrays.asList(1), results.get("update test set text_column = '111' where id = 1;update test set text_column = '333' where id = 3").getRows().get(0));
            assertEquals(Arrays.asList(0), results.get("update test set text_column = '111' where id = 1;update test set text_column = '333' where id = 3").getRows().get(1));

        }
    }


    private static class MySqlCaptureAbstract extends SqlCaptureAbstract implements SqlDataCapture {
        final ArrayList<String> ranSql = new ArrayList<>();
        final Map<String, QueryResults> results = new HashMap<>();


        @Override
        public void afterRun(Object sql, long startTime) {
            ranSql.add(sql.toString());
        }

        @Override
        public void onComplete(Object sql, long startTime, QueryResults results) {
            this.results.put(sql.toString(), results);
        }
    }
}

