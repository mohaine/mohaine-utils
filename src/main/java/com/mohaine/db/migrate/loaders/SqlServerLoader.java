package com.mohaine.db.migrate.loaders;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.mohaine.db.migrate.ColumnDef;
import com.mohaine.db.migrate.ColumnDef.Type;

public class SqlServerLoader extends BaseLoader {

	public SqlServerLoader(Connection connection) {
		super(connection);
	}

	@Override
	public String getSearchSchemea() {
		return "dbo";
	}

	@Override
	public void appendColumnName(StringBuilder sb, String name) {
		sb.append('[');
		sb.append(name);
		sb.append(']');
	}

	@Override
	public void appendTableName(StringBuilder sb, String name) {
		sb.append('[');
		sb.append(name);
		sb.append(']');
	}

	@Override
	protected String cleanDefault(String columnDefault) {
		if (columnDefault != null) {
			if (columnDefault.startsWith("((") && columnDefault.endsWith("))")) {
				columnDefault = columnDefault.substring(2, columnDefault.length() - 2);
			}
		}
		return columnDefault;
	}

	@Override
	protected Type getGenericColumnType(String tableName, String columnName, String columnTypeName, int columnDisplaySize) {

		if ("uniqueidentifier".equals(columnTypeName)) {
			return Type.ID;
		}
		if ("datetime".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("datetime2".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("timestamp".equals(columnTypeName)) {
			return Type.DateTime;
		}
		if ("varchar".equals(columnTypeName) || "char".equals(columnTypeName)) {
			if (columnDisplaySize > 100000) {
				return Type.Clob;
			}
			return Type.String;
		}

		if ("text".equals(columnTypeName)) {
			return Type.Clob;
		}
		if ("int".equals(columnTypeName)) {
			return Type.Int;
		}
		if ("smallint".equals(columnTypeName)) {
			return Type.Int;
		}
		if ("bigint".equals(columnTypeName)) {
			return Type.Long;
		}
		if ("bit".equals(columnTypeName)) {
			return Type.Boolean;
		}
		if ("float".equals(columnTypeName) || "numeric".equals(columnTypeName)) {
			return Type.Decimal;
		}
		if ("varbinary".equals(columnTypeName)) {
			return Type.Blob;
		}
		if ("image".equals(columnTypeName)) {
			return Type.Blob;
		}

		return Type.Unknown;
	}

	@Override
	public String getDbColumnAlter(ColumnDef columnDef) {
		return " alter column " + columnDef.getName() + " " + getDbColumnType(columnDef);
	}

	@Override
	public String getDbColumnAdd(ColumnDef columnDef) {
		return " add column " + columnDef.getName() + getDbColumnType(columnDef);
	}

	@Override
	protected String getDbColumnType(ColumnDef columnDef) {
		switch (columnDef.getType()) {
		case ID:
			return "uniqueidentifier";
		case Clob:
			return "text";
		case String:
			return "varchar(max)";
		case Decimal:
			return "float";
		case Boolean:
			return "bit";
		case Int:
			return "int";
		case Long:
			return "bigint";
		case DateTime:
			return "datetime";
		case Blob:
			return "varbinary(max)";
		default:
			return null;
		}
	}

	public static boolean isValidFor(DatabaseMetaData metadata) throws SQLException {
		return "Microsoft SQL Server".equals(metadata.getDatabaseProductName());
	}

}
