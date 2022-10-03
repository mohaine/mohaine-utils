package com.mohaine.db.metadata;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.metadata.vendor.H2Apply;
import com.mohaine.db.metadata.vendor.H2Info;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseMetadata_UT {

    @Test
    public void testLoadSave() throws Exception {

        Class.forName("org.h2.Driver");

        var conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");

        DatabaseUtils.execute(conn, "create table test (id INTEGER, text_column varchar(20) default 'abc',  PRIMARY KEY (id),\n)");


        var metadata = new H2Info(conn).loadSchema(conn.getSchema());
        var table = metadata.findTable("test");


        validateTestSchema(metadata);


        var conn2 = DriverManager.getConnection("jdbc:h2:mem:myDb2;DB_CLOSE_DELAY=-1");
        var apply = new H2Apply(conn2);

        apply.createTable(table);
        var metadataBack = new H2Info(conn2).loadSchema(conn2.getSchema());
        validateTestSchema(metadataBack);


        String json = metadata.toJson();
        System.out.println(json);
        var jsonAndBack = SchemaDef.fromJson(json);
        validateTestSchema(jsonAndBack);

    }

    private void validateTestSchema(SchemaDef metadata) {

        assertEquals(1, metadata.tables().size());
        var table = metadata.findTable("test");
        assertEquals(2, table.columns().size());


        var idColumn = table.findColumn("id");
        assertEquals(ColumnDef.DataType.Int, idColumn.type().type());
        assertTrue(idColumn.indexed());

        var textColumn = table.findColumn("text_column");
        assertEquals("'abc'", textColumn.defaultValue());
        assertEquals(Integer.valueOf(20), textColumn.type().maxSize());
        assertEquals(ColumnDef.DataType.String, textColumn.type().type());
        assertFalse(textColumn.indexed());


    }

    private TableDef addTable(String name) {
        ArrayList<ColumnDef> columns = new ArrayList<ColumnDef>();

        columns.add(new ColumnDef(name + "c1", new ColumnDef.Type(ColumnDef.DataType.Decimal), 1));
        columns.add(new ColumnDef(name + "c2", new ColumnDef.Type(ColumnDef.DataType.String, -1, 10), 2));
        return new TableDef(name, columns);
    }
}

