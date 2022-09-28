package com.mohaine.db.metadata.vendor;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.metadata.ColumnDef;
import com.mohaine.db.metadata.ColumnDef.Type;
import com.mohaine.db.metadata.DbShim;
import com.mohaine.db.metadata.SchemaMetadata;
import com.mohaine.db.metadata.TableDef;
import com.mohaine.util.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public abstract class GenericVendorShim implements DbShim {

    protected static final String INDEX_PREFIX = "";

    protected Connection conn;
    private Logger logger;

    public GenericVendorShim(Connection connection) {
        this.conn = connection;

        this.logger = Logger.getLogger(getClass().getName());
    }

    public String getSearchSchemea() throws SQLException {
        return conn.getSchema();
    }

    protected abstract Type getGenericColumnType(String tableName, String columnName, String columnTypeName, int columnDisplaySize);

    protected abstract String getDbColumnType(ColumnDef columnDef);

    protected String getSearchCatalog() {
        return null;
    }

    @Override
    public SchemaMetadata loadMetadata() throws Exception {
        List<TableDef> tableDefs = new ArrayList<TableDef>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tablesRs = metaData.getTables(getSearchCatalog(), getSearchSchemea(), null, new String[]{"TABLE"});
        try {
            while (tablesRs.next()) {
                String tableName = tablesRs.getString("TABLE_NAME");
                TableDef def = loadTableDef(tableName);

                if (def != null) {
                    tableDefs.add(def);
                }
            }
        } finally {
            DatabaseUtils.close(tablesRs);
        }
        return new SchemaMetadata(tableDefs);
    }

    protected TableDef loadTableDef(String tableName) throws SQLException {

        DatabaseMetaData metaData = conn.getMetaData();


        List<ColumnDef> columns = new ArrayList<ColumnDef>();

        ResultSet indexRs = metaData.getIndexInfo(getSearchCatalog(), getSearchSchemea(), tableName, false, false);
        Set<String> indexedColumSet = new HashSet<String>();
        try {
            while (indexRs.next()) {
                String indexName = indexRs.getString("INDEX_NAME");
                if (indexName != null && isBuiltInIndex(indexName)) {
                    String columnName = indexRs.getString("COLUMN_NAME");
                    indexedColumSet.add(columnName.toUpperCase());
                }
            }
        } finally {
            DatabaseUtils.close(indexRs);
        }

        ResultSet columnsRs = metaData.getColumns(getSearchCatalog(), getSearchSchemea(), tableName, null);
        try {
            while (columnsRs.next()) {


                String columnName = columnsRs.getString("COLUMN_NAME");
                int columnSize = columnsRs.getInt("COLUMN_SIZE");
                String columnType = columnsRs.getString("TYPE_NAME");
                String columnDefault = cleanDefault(columnsRs.getString("COLUMN_DEF"));


                Type type = getGenericColumnType(tableName, columnName, columnType, columnSize);
                if (Type.DateTime.equals(type)) {
                    columnSize = 0;
                }

                boolean nullable = DatabaseMetaData.attributeNoNulls == columnsRs.getInt("NULLABLE");
                boolean indexed = indexedColumSet.contains(columnName.toUpperCase());

                boolean key = "id".equalsIgnoreCase(columnName);
                ColumnDef colDef = new ColumnDef(columnName, type, columnSize, key, indexed, columnDefault, nullable);
                columns.add(colDef);
            }
        } finally {
            DatabaseUtils.close(columnsRs);
        }

        TableDef def = new TableDef(tableName, columns);
        return def;
    }

    protected boolean isBuiltInIndex(String indexName) {
        return indexName.startsWith(INDEX_PREFIX);
    }

    protected String cleanDefault(String columnDefault) {
        return columnDefault;
    }

    @Override
    public void recreateTable(TableDef tableDef) throws Exception {
        if (tableExists(tableDef.getName())) {
            dropTable(tableDef);
        }
        createTable(tableDef);
    }

    private boolean tableExists(String tableName) throws SQLException {
        try {
            TableDef def = loadTableDef(tableName);
            return def != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void dropTable(TableDef tableDef) throws Exception {
        logger.severe("Drop Table: " + tableDef.getName());

        StringBuilder sb = new StringBuilder();
        sb.append("drop table ");
        sb.append(tableDef.getName());
        DatabaseUtils.execute(conn, sb.toString());
    }


    @Override
    public void updateStructureToMatch(SchemaMetadata metadata) throws Exception {

        SchemaMetadata existing = loadMetadata();

        Map<String, TableDef> existingTables = new HashMap<String, TableDef>();
        for (TableDef tableDef : existing.getTables()) {
            existingTables.put(tableDef.getName().toLowerCase(), tableDef);
        }

        for (TableDef tableDef : metadata.getTables()) {
            TableDef existingTableDef = existingTables.get(tableDef.getName().toLowerCase());
            if (existingTableDef == null) {
                createTable(tableDef);
            } else {
                alterTable(tableDef, existingTableDef);
            }
        }
    }

    private void alterTable(TableDef tableDef, TableDef existingTableDef) throws SQLException {
        Map<String, ColumnDef> existingColumns = new HashMap<String, ColumnDef>();
        for (ColumnDef columnDef : existingTableDef.getColumns()) {
            existingColumns.put(columnDef.getName().toLowerCase(), columnDef);
        }

        for (ColumnDef columnDef : tableDef.getColumns()) {
            ColumnDef existingColumnDef = existingColumns.get(columnDef.getName().toLowerCase());

            if (existingColumnDef == null) {

                logger.fine("Alter Table For Column: " + tableDef.getName() + "." + columnDef.getName());

                StringBuilder sb = new StringBuilder();
                sb.append("alter table ");
                appendTableName(sb, tableDef.getName());
                sb.append(" add ");
                appendColumnName(sb, columnDef.getName());
                sb.append(" ");
                sb.append(getDbColumnTypeVerify(tableDef, columnDef));
                sb.append(" ");
                appendCreateExtra(sb, columnDef);
                DatabaseUtils.execute(conn, sb.toString());
            } else {
                if (columnDef.getType() == Type.String) {
                    if (columnDef.getLength() > existingColumnDef.getLength()) {
                        alterTextLength(tableDef, columnDef);
                    }
                }
            }

            if (columnDef.isIndexed() && (existingColumnDef == null || !existingColumnDef.isIndexed())) {
                createIndex(tableDef, columnDef);
            }
        }

    }

    protected void alterTextLength(TableDef tableDef, ColumnDef columnDef) throws SQLException {
        logger.fine("Alter Text Column Length For: " + tableDef.getName() + "." + columnDef.getName());

        StringBuilder sb = new StringBuilder();
        sb.append("alter table ");
        appendTableName(sb, tableDef.getName());
        sb.append(" alter column ");
        appendColumnName(sb, columnDef.getName());
        sb.append(" ");
        sb.append(getDbColumnTypeVerify(tableDef, columnDef));
        DatabaseUtils.execute(conn, sb.toString());
    }

    public String getDbColumnTypeVerify(TableDef tableDef, ColumnDef columnDef) {
        String type = getDbColumnType(columnDef);
        if (type == null) {
            throw new RuntimeException("Unhandled Type: '" + columnDef.getType() + "' for " + tableDef.getName() + "." + columnDef.getName());
        }
        return type;
    }

    private void createTable(TableDef tableDef) throws SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("create table ");

        appendTableName(sb, tableDef.getName());

        sb.append("(\r\n");

        int columnIndex = 0;
        List<ColumnDef> columns = tableDef.getColumns();
        for (ColumnDef columnDef : columns) {

            if (columnIndex > 0) {
                sb.append(",\r\n");
            }

            appendColumnName(sb, columnDef.getName());
            sb.append(" ");
            sb.append(getDbColumnTypeVerify(tableDef, columnDef));
            sb.append(" ");
            appendCreateExtra(sb, columnDef);

            columnIndex++;
        }

        sb.append("\r\n)");

        Logger.getLogger(getClass().getName()).fine("Create Table: " + sb.toString());


        System.out.println(sb.toString());

        DatabaseUtils.execute(conn, sb.toString());

        for (ColumnDef columnDef : columns) {
            if (columnDef.isIndexed()) {
                createIndex(tableDef, columnDef);
            }
        }
    }

    protected void appendCreateExtra(StringBuilder sb, ColumnDef columnDef) {
        if (columnDef.isKey()) {
            sb.append(" PRIMARY KEY NOT NULL");
        } else if (columnDef.isNotNull()) {
            sb.append(" NOT NULL");
        }
        appendDefault(sb, columnDef);
    }

    protected void appendDefault(StringBuilder sb, ColumnDef columnDef) {
        if (StringUtils.hasLength(columnDef.getDefaultValue())) {
            sb.append(" default ");

            Type type = columnDef.getType();
            System.out.println("type: " + type);
            if (type == Type.Clob || type == Type.String) {
                sb.append("'");
            }

            sb.append(columnDef.getDefaultValue());

            if (type == Type.Clob || type == Type.String) {
                sb.append("'");
            }
        }
    }


    protected void createIndex(TableDef tableDef, ColumnDef columnDef) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append("create index ");

        appendIndexName(sb, tableDef, columnDef);

        sb.append(" on ");
        appendTableName(sb, tableDef.getName());
        sb.append(" ( ");
        appendColumnName(sb, columnDef.getName());
        sb.append(" ) ");
        logger.fine("Create Index For: " + tableDef.getName() + "." + columnDef.getName());

        DatabaseUtils.execute(conn, sb.toString());
    }

    protected void appendIndexName(StringBuilder sb, TableDef tableDef, ColumnDef columnDef) {
        sb.append(INDEX_PREFIX);
        sb.append(tableDef.getName());
        sb.append("_");
        sb.append(columnDef.getName());
    }
}
