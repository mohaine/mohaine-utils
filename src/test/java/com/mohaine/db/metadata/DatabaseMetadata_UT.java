package com.mohaine.db.metadata;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.PrintableConnectionProxy;
import com.mohaine.db.SqlPrinterConsole;
import com.mohaine.db.metadata.ColumnDef.Type;
import com.mohaine.json.JsonObjectConverter;
//import com.mohaine.json.ReflectionJsonHandler;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.ArrayList;

public class DatabaseMetadata_UT {

    @Test
    public void testLoadSave() throws Exception {

        Class.forName("org.h2.Driver");

        var conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");


        DatabaseUtils.execute(conn, "create table test (number NUMBER, text varchar(20) default 'abc',  PRIMARY KEY (number),\n)");


        var loader = DatabaseMetadata.createShimFor(new PrintableConnectionProxy(conn, new SqlPrinterConsole()));


        var metadata = loader.loadMetadata();

        JsonObjectConverter joc = new JsonObjectConverter();
//        joc.addHandler(ReflectionJsonHandler.build(SchemaMetadata.class));
//        joc.addHandler(ReflectionJsonHandler.build(TableDef.class));
//        joc.addHandler(ReflectionJsonHandler.build(ColumnDef.class));

        System.out.println("json = " + joc.encode(metadata));

        var metaDataBack = joc.decode(joc.encode(metadata), SchemaMetadata.class);

        System.out.println("json = " + joc.encode(metaDataBack));

//        assertEquals(1, metadata.getTables().size());
//
//
//        System.out.println(metadata.getTables().get(0).getColumns());
//
//        DatabaseMetadata dm = new DatabaseMetadata();
//
//        ArrayList<TableDef> tables = new ArrayList<TableDef>();
//        metadata.setTables(tables);
//        tables.add(addTable("T1"));
//        tables.add(addTable("T2"));
//        tables.add(addTable("T3"));
//        loader.updateStructureToMatch(metadata);
    }

    private TableDef addTable(String name) {
        ArrayList<ColumnDef> columns = new ArrayList<ColumnDef>();

        columns.add(new ColumnDef(name + "c1", Type.Decimal));
        columns.add(new ColumnDef(name + "c2", Type.String, 10));
        return new TableDef(name, columns);
    }
}
