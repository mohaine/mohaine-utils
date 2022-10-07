package com.mohaine.db.metadata.vendor;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.metadata.ColumnDef;
import com.mohaine.db.metadata.ColumnDef.Type;
import com.mohaine.db.metadata.DbInfo;
import com.mohaine.db.metadata.SchemaDef;
import com.mohaine.db.metadata.TableDef;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenericVendorInfo implements DbInfo {

    protected static final String INDEX_PREFIX = "";

    protected Connection conn;
    private String searchCatalog;

    public GenericVendorInfo(Connection connection) throws SQLException {
        this.conn = connection;
        this.searchCatalog = conn.getCatalog();

    }


    public Type getGenericColumnType(RawColumnInfo colunInfo) {

        var columnTypeName = colunInfo.typeName.toLowerCase();

        if ("char".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.String, colunInfo.columnSize, colunInfo.columnSize);
        }
        if ("timestamp".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.DateTime);
        }
        if ("varchar".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.String, -1, colunInfo.columnSize);
        }

        if ("clob".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Clob);
        }
        if ("int".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Int);
        }
        if ("integer".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Int);
        }
        if ("smallint".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Boolean);
        }
        if ("bigint".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Long);
        }
        if ("bit".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Boolean);
        }
        if ("float".equals(columnTypeName) || "decimal".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Decimal);
        }
        if ("blob".equals(columnTypeName)) {
            return new Type(ColumnDef.DataType.Blob);
        }
        if ("numeric".equals(columnTypeName)) {
            if (colunInfo.decimalDigits != null) {
                if (colunInfo.decimalDigits == 0) {
                    return new Type(ColumnDef.DataType.Int);
                }
            }
            return new Type(ColumnDef.DataType.Decimal);
        }

        return new Type(ColumnDef.DataType.Unknown);
    }

//    protected abstract String getDbColumnType(ColumnDef columnDef);


    @Override
    public SchemaDef loadSchema(String schema) throws Exception {
        List<TableDef> tableDefs = new ArrayList<TableDef>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tablesRs = metaData.getTables(searchCatalog, schema, null, new String[]{"TABLE"});
        try {
            while (tablesRs.next()) {
                String tableName = tablesRs.getString("TABLE_NAME");
                TableDef def = loadTable(schema, tableName);

                if (def != null) {
                    tableDefs.add(def);
                }
            }
        } finally {
            DatabaseUtils.close(tablesRs);
        }
        return new SchemaDef(schema, tableDefs);
    }


    public record RawColumnInfo(
            String columnName,
            Integer columnSize,
            String typeName,
            String columnDefault,
            String columnRemarks,
            Integer postition,
            boolean autoincrement,
            Integer decimalDigits,
            boolean nullable
    ) {
    }

    @Override
    public TableDef loadTable(String schema, String tableName) throws Exception {

        DatabaseMetaData metaData = conn.getMetaData();

        List<ColumnDef> columns = new ArrayList<ColumnDef>();

        ResultSet indexRs = metaData.getIndexInfo(searchCatalog, schema, tableName, true, false);
        Set<String> indexedColumSet = new HashSet<String>();
        try {
            while (indexRs.next()) {
                String indexName = indexRs.getString("INDEX_NAME");
                if (indexName != null) {
                    String columnName = indexRs.getString("COLUMN_NAME");
                    indexedColumSet.add(columnName.toUpperCase());
                }
            }
        } finally {
            DatabaseUtils.close(indexRs);
        }

        ResultSet columnsRs = metaData.getColumns(searchCatalog, schema, tableName, null);

        try {
            while (columnsRs.next()) {


//                TABLE_CAT String => table catalog (may be null)
//                TABLE_SCHEM String => table schema (may be null)
//                TABLE_NAME String => table name
//                COLUMN_NAME String => column name
//                DATA_TYPE int => SQL type from java.sql.Types
//                TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
//                COLUMN_SIZE int => column size.
//                BUFFER_LENGTH is not used.
//                DECIMAL_DIGITS int => the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
//                        NUM_PREC_RADIX int => Radix (typically either 10 or 2)
//                NULLABLE int => is NULL allowed.
//                        columnNoNulls - might not allow NULL values
//                columnNullable - definitely allows NULL values
//                columnNullableUnknown - nullability unknown
//                REMARKS String => comment describing column (may be null)
//                COLUMN_DEF String => default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
//                    SQL_DATA_TYPE int => unused
//                    SQL_DATETIME_SUB int => unused
//                    CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
//                    ORDINAL_POSITION int => index of column in table (starting at 1)
//                    IS_NULLABLE String => ISO rules are used to determine the nullability for a column.
//                    YES --- if the column can include NULLs
//                    NO --- if the column cannot include NULLs
//                    empty string --- if the nullability for the column is unknown
//                    SCOPE_CATALOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
//                    SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
//                    SCOPE_TABLE String => table name that this the scope of a reference attribute (null if the DATA_TYPE isn't REF)
//                    SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
//                    IS_AUTOINCREMENT String => Indicates whether this column is auto incremented
//                    YES --- if the column is auto incremented
//                    NO --- if the column is not auto incremented
//                    empty string --- if it cannot be determined whether the column is auto incremented
//                    IS_GENERATEDCOLUMN

                String columnName = columnsRs.getString("COLUMN_NAME");
                Integer columnSize = columnsRs.getInt("COLUMN_SIZE");
                String columnType = columnsRs.getString("TYPE_NAME");
                String columnDefault = cleanDefault(columnsRs.getString("COLUMN_DEF"));
                String columnRemarks = cleanDefault(columnsRs.getString("REMARKS"));
                Integer postition = columnsRs.getInt("ORDINAL_POSITION");
                boolean autoincrement = columnsRs.getBoolean("IS_AUTOINCREMENT");
                Integer decimalDigits = columnsRs.getInt("DECIMAL_DIGITS");
                boolean nullable = DatabaseMetaData.attributeNoNulls != columnsRs.getInt("NULLABLE");
                boolean indexed = indexedColumSet.contains(columnName.toUpperCase());

                var columnInfo = new RawColumnInfo(columnName, columnSize, columnType, columnDefault, columnRemarks, postition, autoincrement, decimalDigits, nullable);

                Type type = getGenericColumnType(columnInfo);
                if (type.type() == ColumnDef.DataType.Unknown) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown column: " + tableName + "." + columnName + ": ");
                    ResultSetMetaData columnsRsMetaData = columnsRs.getMetaData();
                    for (int i = 1; i <= columnsRsMetaData.getColumnCount(); i++) {
                        if (i > 1) {
                            sb.append(", ");
                        }
                        sb.append(columnsRsMetaData.getColumnName(i) + ":" + columnsRs.getObject(i));
                    }
                }

                if (ColumnDef.DataType.DateTime.equals(type)) {
                    columnSize = 0;
                }

                boolean key = "id".equalsIgnoreCase(columnName);
                ColumnDef colDef = new ColumnDef(columnName, type, key, indexed, columnDefault, nullable, columnRemarks, postition, autoincrement);
                columns.add(colDef);
            }
        } finally {
            DatabaseUtils.close(columnsRs);
        }

        TableDef def = new TableDef(tableName, columns);
        return def;
    }


    protected String cleanDefault(String columnDefault) {
        return columnDefault;
    }


}

