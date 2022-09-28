package com.mohaine.db.migrate.loaders;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.mohaine.db.migrate.ColumnDef;
import com.mohaine.db.migrate.ColumnDef.Type;
import com.mohaine.db.migrate.TableDef;
import com.mohaine.util.StringUtils;

public class OracleLoader extends BaseLoader {

	private String username;

	public OracleLoader(Connection connection) {
		super(connection);

		try {
			username = connection.getMetaData().getUserName();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
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
	public String getSearchSchemea() {
		return username;
	}

	@Override
	protected void appendDefault(StringBuilder sb, ColumnDef columnDef) {
		// Not Null Default 0 is throwing an error so no defaults for oracle
	}

	@Override
	protected String getDbColumnType(ColumnDef columnDef) {
		switch (columnDef.getType()) {
		case ID:
			return "CHAR(36)";
		case Clob:
			return "CLOB";
		case String:
			return "varchar2(" + columnDef.getLength() + ")";
		case Decimal:
			return "NUMBER(31,5) ";
		case Boolean:
			return "NUMBER(1)";
		case Int:
			return "NUMBER(31)";
		case Long:
			return "NUMBER(38)";
		case DateTime:
			return "TIMESTAMP";
		case Blob:
			return "BLOB";
		default:
			return null;
		}
	}

	@Override
	protected Type getGenericColumnType(String tableName, String columnName, String columnTypeName, int columnDisplaySize) {
		columnTypeName = columnTypeName.toLowerCase();
		if ("char".equals(columnTypeName)) {
			return Type.String;
		}
		if ("date".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("timestamp(6)".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("varchar".equals(columnTypeName)) {
			return Type.String;
		}

		if ("varchar2".equals(columnTypeName)) {
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

		if ("number".equals(columnTypeName)) {

			if (columnDisplaySize == 1) {
				return Type.Boolean;
			}

			return Type.Decimal;
		}

		return Type.Unknown;
	}

	@Override
	public String getDbColumnAlter(ColumnDef columnDef) {
		return " alter column " + columnDef.getName() + " " + getDbColumnType(columnDef);
	}

	@Override
	public String getDbColumnAdd(ColumnDef columnDef) {
		return " add column " + columnDef.getName() + " " + getDbColumnType(columnDef);
	}

	public static boolean isValidFor(DatabaseMetaData metadata) throws SQLException {
		return "Oracle".equals(metadata.getDatabaseProductName());
	}

	@Override
	public void appendIndexName(StringBuilder sb, TableDef tableDef, ColumnDef columnDef) {
		String columnName = columnDef.getName();
		String tableName = tableDef.getName();

		while (INDEX_PREFIX.length() + 1 + tableName.length() + columnName.length() > 30) {
			if (tableName.length() >= columnName.length()) {
				tableName = tableName.substring(0, tableName.length() - 1);
			} else {
				columnName = columnName.substring(0, columnName.length() - 1);
			}
		}

		sb.append(INDEX_PREFIX);
		sb.append(tableName);
		sb.append("_");
		sb.append(columnName);
	}

}
