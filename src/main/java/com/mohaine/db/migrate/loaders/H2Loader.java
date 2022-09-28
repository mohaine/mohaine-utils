package com.mohaine.db.migrate.loaders;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.migrate.ColumnDef;
import com.mohaine.db.migrate.ColumnDef.Type;
import com.mohaine.db.migrate.TableDef;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class H2Loader extends BaseLoader {

	public H2Loader(Connection connection) {
		super(connection);
	}

	@Override
	public void appendColumnName(StringBuilder sb, String name) {
		sb.append('"');
		sb.append(name.toUpperCase());
		sb.append('"');
	}

	@Override
	public void appendTableName(StringBuilder sb, String name) {
		sb.append('"');
		sb.append(name.toUpperCase());
		sb.append('"');
	}

	@Override
	protected Type getGenericColumnType(String tableName, String columnName, String columnTypeName, int columnDisplaySize) {
		columnTypeName = columnTypeName.toLowerCase();
		if ("char".equals(columnTypeName)) {
			return Type.String;
		}
		if ("timestamp".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("varchar".equals(columnTypeName)) {
			return Type.String;
		}

		if ("clob".equals(columnTypeName)) {
			return Type.Clob;
		}
		if ("int".equals(columnTypeName)) {
			return Type.Int;
		}
		if ("integer".equals(columnTypeName)) {
			return Type.Int;
		}
		if ("smallint".equals(columnTypeName)) {
			return Type.Boolean;
		}
		if ("bigint".equals(columnTypeName)) {
			return Type.Long;
		}
		if ("bit".equals(columnTypeName)) {
			return Type.Boolean;
		}
		if ("float".equals(columnTypeName) || "decimal".equals(columnTypeName)) {
			return Type.Decimal;
		}
		if ("blob".equals(columnTypeName)) {
			return Type.Blob;
		}

		return Type.Unknown;
	}

	@Override
	public String getDbColumnAlter(ColumnDef columnDef) {
		return " alter column " + columnDef.getName() + " set data type " + getDbColumnType(columnDef);
	}

	@Override
	public String getDbColumnAdd(ColumnDef columnDef) {
		return " add column " + columnDef.getName() + " " + getDbColumnType(columnDef);
	}

	@Override
	protected String getDbColumnType(ColumnDef columnDef) {
		switch (columnDef.getType()) {
		case ID:
			return "CHAR(36)";
		case Clob:
			return "CLOB";
		case String:
			return "varchar(" + columnDef.getLength() + ")";
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
		if (columnDef.isKey()) {
			sb.append("  NOT NULL auto_increment");
		} else if (columnDef.isNotNull()) {
			sb.append(" NOT NULL");
		}
		appendDefault(sb, columnDef);
	}


	public static boolean isValidFor(DatabaseMetaData metadata) throws SQLException {
		return "H2".equals(metadata.getDatabaseProductName());
	}

	@Override
	protected void alterTextLength(TableDef tableDef, ColumnDef columnDef) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("alter table ");
		appendTableName(sb, tableDef.getName());
		sb.append(" alter ");
		appendColumnName(sb, columnDef.getName());
		sb.append(" SET DATA TYPE ");
		sb.append(getDbColumnTypeVerify(tableDef, columnDef));
		DatabaseUtils.execute(conn, sb.toString());
	}

}
