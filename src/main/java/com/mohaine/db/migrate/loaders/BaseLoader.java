package com.mohaine.db.migrate.loaders;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.migrate.*;
import com.mohaine.db.migrate.ColumnDef.Type;
import com.mohaine.db.orm.engine.KeyedObjectTableMapping;
import com.mohaine.db.orm.engine.ObjectTableMapping;
import com.mohaine.util.StringUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public abstract class BaseLoader implements DbLoader {

	protected static final String INDEX_PREFIX = "QI_";

	public class DataWrapper implements Serializable {
		private Object data;

		public DataWrapper(Object value) {
			this.data = value;
		}

	}

	protected Connection conn;
	private Logger logger;

	public BaseLoader(Connection connection) {
		this.conn = connection;

		this.logger = Logger.getLogger(getClass().getName());
	}

	public  String getSearchSchemea() throws SQLException {
		return conn.getSchema();
	}

	protected abstract Type getGenericColumnType(String tableName, String columnName, String columnTypeName, int columnDisplaySize);

	protected abstract String getDbColumnType(ColumnDef columnDef);

	protected String getSearchCatalog() {
		return null;
	}

	@Override
	public DbMetadata loadMetadata() throws Exception {
		List<TableDef> tableDefs = new ArrayList<TableDef>();
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet tablesRs = metaData.getTables(getSearchCatalog(), getSearchSchemea(), null, new String[] { "TABLE" });
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
		return new DbMetadata(tableDefs);
	}

	protected TableDef loadTableDef(String tableName) throws SQLException {

		DatabaseMetaData metaData = conn.getMetaData();

		TableDef def = new TableDef();
		def.setName(tableName);

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

				ColumnDef colDef = new ColumnDef();
				columns.add(colDef);

				String columnName = columnsRs.getString("COLUMN_NAME");
				int columnSize = columnsRs.getInt("COLUMN_SIZE");
				String columnType = columnsRs.getString("TYPE_NAME");
				String columnDefault = cleanDefault(columnsRs.getString("COLUMN_DEF"));

				colDef.setName(columnName);

				Type type = getGenericColumnType(tableName, columnName, columnType, columnSize);
				if (Type.DateTime.equals(type)) {
					columnSize = 0;
				}

				colDef.setType(type);
				colDef.setLength(columnSize);
				colDef.setKey("id".equalsIgnoreCase(columnName));
				colDef.setNotNull(DatabaseMetaData.attributeNoNulls == columnsRs.getInt("NULLABLE"));
				colDef.setDefaultValue(columnDefault);
				colDef.setIndexed(indexedColumSet.contains(columnName.toUpperCase()));
			}
		} finally {
			DatabaseUtils.close(columnsRs);
		}

		def.setColumns(columns);

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
	public void updateStructureToMatch(DbMetadata metadata) throws Exception {

		DbMetadata existing = loadMetadata();

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


		System.out.println( sb.toString());

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
