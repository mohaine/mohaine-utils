package com.mohaine.db.orm;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.metadata.ColumnDef;
import com.mohaine.db.metadata.DatabaseDef;
import com.mohaine.db.metadata.SchemaDef;
import com.mohaine.db.metadata.TableDef;
import com.mohaine.db.metadata.vendor.H2Apply;
import com.mohaine.db.metadata.vendor.H2Info;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class OrmLoader_UT {

    @DbTable(tableName = "test")
    public static class TestObject {
        @DbField(key = true, type = DbType.Integer, columnName = "id", callSetter = true)
        private Integer id;
        @DbField(type = DbType.String, columnName = "text_column", callSetter = true)
        private String textColumn;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTextColumn() {
            return textColumn;
        }

        public void setTextColumn(String textColumn) {
            this.textColumn = textColumn;
        }
    }

    @Test
    public void testLoadSave() throws Exception {


        Class.forName("org.h2.Driver");

        try (var conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1")) {

            DatabaseUtils.execute(conn, "create table test (id INTEGER, text_column varchar(20) default 'abc',  PRIMARY KEY (id),\n)");

            OrmHandler<TestObject> handler = new OrmHandler<TestObject>(conn, TestObject.class);

            var original = new TestObject();
            original.setId(1);
            original.setTextColumn("Some Orginal Text");
            handler.saveObject(original);

            assertNotNull(original.getId());

            OrmLoader<TestObject> loader = new OrmLoader<TestObject>(conn, "Select * from test where id = 1", TestObject.class);

            var results = loader.getObjectsArrayList();

            assertEquals(1, results.size());
            assertEquals(original.getTextColumn(), results.get(0).getTextColumn());
        }
    }


}

