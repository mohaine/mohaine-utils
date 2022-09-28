package com.mohaine.db.migrate;

import static org.junit.Assert.*;

import java.sql.DriverManager;
import java.util.ArrayList;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.PrintableConnectionProxy;
import com.mohaine.db.SqlPrinter;
import com.mohaine.db.SqlPrinterConsole;
import org.junit.Test;

import com.mohaine.db.migrate.ColumnDef.Type;

public class DatabaseMigrage_UT {

    @Test
    public void testLoadSave() throws Exception {

		Class.forName("org.h2.Driver");

        var conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");


        DatabaseUtils.execute(conn,"create table test (number NUMBER, text varchar(20) default 'abc' )");



        var loader = DatabaseMigrate.createLoaderFor(new PrintableConnectionProxy(conn, new SqlPrinterConsole()));


        var metadata = loader.loadMetadata();
        assertEquals(1,metadata.getTables().size());

        System.out.println(metadata.getTables().get(0).getColumns());

        DatabaseMigrate dm = new DatabaseMigrate();

        ArrayList<TableDef> tables = new ArrayList<TableDef>();
        metadata.setTables(tables);
        tables.add(addTable("T1"));
        tables.add(addTable("T2"));
        tables.add(addTable("T3"));


        loader.updateStructureToMatch(metadata);
    }

    private TableDef addTable(String name) {
        ArrayList<ColumnDef> columns = new ArrayList<ColumnDef>();
        TableDef table1 = new TableDef();
        table1.setName(name);
        table1.setColumns(columns);

        ColumnDef col1 = new ColumnDef();
        col1.setDefaultValue(null);
        col1.setKey(true);
        col1.setLength(1);
        col1.setName(table1.getName() + "Col1");
        col1.setNotNull(true);
        col1.setType(Type.Decimal);
        columns.add(col1);
        ColumnDef col2 = new ColumnDef();
        col2.setDefaultValue(table1.getName() + "Def2");
        col2.setKey(false);
        col2.setLength(2);
        col2.setName(table1.getName() + "Col2");
        col2.setNotNull(false);
        col2.setType(Type.String);
        columns.add(col2);
        return table1;
    }
}
