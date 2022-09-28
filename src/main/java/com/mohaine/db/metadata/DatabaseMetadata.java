package com.mohaine.db.metadata;

import com.mohaine.db.metadata.vendor.H2Shim;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DatabaseMetadata {

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {


        DatabaseMetadata dm = new DatabaseMetadata();

    }

    public static DbShim createShimFor(Connection connection) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();

        if (H2Shim.isValidFor(metadata)) {
            return new H2Shim(connection);
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
