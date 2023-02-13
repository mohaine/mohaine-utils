package com.mohaine.db.metadata.vendor;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.metadata.ColumnDef;
import com.mohaine.db.metadata.DbApply;
import com.mohaine.db.metadata.TableDef;
import com.mohaine.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class GenericVendorApply implements DbApply {


    private final Connection conn;

    public GenericVendorApply(Connection connection) throws SQLException {
        this.conn = connection;

    }

    @Override
    public void createTable(TableDef tableDef) throws SQLException {
        createTable(null, tableDef);
    }

    @Override
    public void createTable(String schemaName, TableDef tableDef) throws SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("create table ");

        if (schemaName != null && !schemaName.isBlank()) {
            appendSchemaName(sb, schemaName);
            sb.append(".");
        }

        appendTableName(sb, tableDef);

        sb.append("(\r\n");

        int columnIndex = 0;
        List<ColumnDef> columns = tableDef.columns();

        columns.sort(new Comparator<ColumnDef>() {
            @Override
            public int compare(ColumnDef o1, ColumnDef o2) {
                return o1.position() - o2.position();
            }
        });

        for (ColumnDef columnDef : columns) {

            if (columnIndex > 0) {
                sb.append(",\r\n");
            }

            appendColumnName(sb, columnDef);
            sb.append(" ");
            sb.append(getDbColumnType(columnDef));
            sb.append(" ");
            appendCreateExtra(sb, columnDef);

            columnIndex++;
        }

        sb.append("\r\n)");

        DatabaseUtils.execute(conn, sb.toString());

        for (ColumnDef columnDef : columns) {
            if (columnDef.indexed()) {
                createIndex(schemaName, tableDef, columnDef);
            }
        }
    }


    protected String getDbColumnType(ColumnDef columnDef) {
        switch (columnDef.type().type()) {
            case ID:
                return "CHAR(" + columnDef.type().size() + ")";
            case Clob:
                return "CLOB";
            case String:
                return "varchar(" + columnDef.type().maxSize() + ")";
            case Decimal:
                return "NUMBER";
            case Boolean:
                return "SMALLINT";
            case Int:
                return "INT";
            case Long:
                return "BIGINT";
            case DateTime:
                return "TIMESTAMP";
            case Blob:
                return "BLOB";
            default:
                return null;
        }
    }

    protected void appendCreateExtra(StringBuilder sb, ColumnDef columnDef) {
        if (columnDef.key()) {
            sb.append(" PRIMARY KEY NOT NULL");
        } else if (!columnDef.nullable()) {
            sb.append(" NOT NULL");
        }
        appendDefault(sb, columnDef);
    }

    protected void appendDefault(StringBuilder sb, ColumnDef columnDef) {
        if (StringUtils.hasLength(columnDef.defaultValue())) {
            sb.append(" default ");
            ColumnDef.Type type = columnDef.type();
            sb.append(columnDef.defaultValue());
        }
    }


    protected void createIndex(String schemaName, TableDef tableDef, ColumnDef columnDef) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append("create index ");

        appendIndexName(sb, tableDef, columnDef);

        sb.append(" on ");

        if (schemaName != null && !schemaName.isBlank()) {
            appendSchemaName(sb, schemaName);
            sb.append(".");
        }

        appendTableName(sb, tableDef);
        sb.append(" ( ");
        appendColumnName(sb, columnDef);
        sb.append(" ) ");

        DatabaseUtils.execute(conn, sb.toString());
    }

    protected void appendSchemaName(StringBuilder sb, String schemaName) {
        sb.append('"');
        sb.append(schemaName);
        sb.append('"');
    }

    protected void appendTableName(StringBuilder sb, TableDef tableDef) {
        sb.append('"');
        sb.append(tableDef.name());
        sb.append('"');
    }

    protected void appendColumnName(StringBuilder sb, ColumnDef columnDef) {
        sb.append('"');
        sb.append(columnDef.name());
        sb.append('"');
    }

    protected void appendIndexName(StringBuilder sb, TableDef tableDef, ColumnDef columnDef) {
        sb.append('"');
        sb.append(tableDef.name());
        sb.append("_");
        sb.append(columnDef.name());
        sb.append('"');
    }


}
