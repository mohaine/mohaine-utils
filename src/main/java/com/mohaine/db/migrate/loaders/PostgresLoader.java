package com.mohaine.db.migrate.loaders;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.mohaine.db.migrate.ColumnDef;
import com.mohaine.db.migrate.ColumnDef.Type;

public class PostgresLoader extends BaseLoader {

	public PostgresLoader(Connection connection) {
		super(connection);
	}

	@Override
	public String getSearchSchemea() {
		return "public";
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
		if ("uuid".equals(columnTypeName)) {
			return Type.ID;
		}
		if ("timestamp".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("text".equals(columnTypeName)) {
			return Type.Clob;
		}
		if ("int4".equals(columnTypeName)) {
			return Type.Int;
		}

		if ("int8".equals(columnTypeName)) {
			return Type.Long;
		}
		if ("bool".equals(columnTypeName)) {
			return Type.Boolean;
		}
		if ("numeric".equals(columnTypeName)) {

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

	@Override
	protected String getDbColumnType(ColumnDef columnDef) {
		switch (columnDef.getType()) {
		case ID:
			return "UUID";
		case Clob:
		case String:
			return "TEXT";
		case Decimal:
			return "NUMERIC";
		case Boolean:
			return "NUMERIC(1)";
		case Int:
			return "integer";
		case Long:
			return "bigint";
		case DateTime:
			return "timestamp";
		default:
			return null;
		}
	}

	public static boolean isValidFor(DatabaseMetaData metadata) throws SQLException {
		return "PostgreSQL".equals(metadata.getDatabaseProductName());
	}

}
