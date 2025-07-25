package com.mohaine.db;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface SqlDataCapture extends SqlCapture {
    public static class QueryResults {
        private final List<String> columns;

        private final List<List<Object>> rows;

        public QueryResults(int[] values) {
            this.columns = null;
            this.rows = new ArrayList<>();
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    this.rows.add(Arrays.asList(values[i]));
                }
            }
        }

        public QueryResults(Integer value) {
            this.columns = null;
            this.rows = new ArrayList<>();
            this.rows.add(Arrays.asList(value));
        }

        public QueryResults(Boolean value) {
            this.columns = null;
            this.rows = new ArrayList<>();
            this.rows.add(Arrays.asList(value));
        }

        public QueryResults(List<String> columns) {
            this.columns = columns;
            this.rows = new ArrayList<>();
        }

        public List<String> getColumns() {
            return columns;
        }

        public List<List<Object>> getRows() {
            return rows;
        }
    }

    public void onComplete(Object sql, long startTime, QueryResults results);
}
