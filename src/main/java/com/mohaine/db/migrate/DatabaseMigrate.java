package com.mohaine.db.migrate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.mohaine.db.PrintableConnectionProxy;
import com.mohaine.db.migrate.loaders.*;
import com.mohaine.util.StreamUtils;

public class DatabaseMigrate {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		int unused = 0;

		DatabaseMigrate dm = new DatabaseMigrate();




//		DatabaseMigrate.createLoaderFor();

	}

	public static DbLoader createLoaderFor(Connection connection) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();

		if (DerbyLoader.isValidFor(metadata)) {
			return new DerbyLoader(connection);
		} else if (H2Loader.isValidFor(metadata)) {
			return new H2Loader(connection);
		} else if (SqlServerLoader.isValidFor(metadata)) {
			return new SqlServerLoader(connection);
		} else if (PostgresLoader.isValidFor(metadata)) {
			return new PostgresLoader(connection);
		} else if (OracleLoader.isValidFor(metadata)) {
			return new OracleLoader(connection);
		} else {
			// System.out.println("Unhandled Datbase: " +
			// metadata.getDatabaseProductName());
		}
		return null;
	}


	public void listTables(Collection<TableDef> metadata) throws Exception {
		for (TableDef tableDef : metadata) {
			// System.out.println(tableDef.getName());
			List<ColumnDef> columns = tableDef.getColumns();
			for (ColumnDef columnDef : columns) {
				System.out.println("     " + columnDef.getName() + " " + columnDef.getType() + " " + columnDef.getLength());
			}
		}

	}
}
