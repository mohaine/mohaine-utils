package com.mohaine.db.orm;

import com.mohaine.db.DatabaseUtils;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrmRecordLoader_UT {

    @Test
    public void testLoadRecord() throws Exception {

        record TestRecord (Integer id, String textColumn){};

        Class.forName("org.h2.Driver");

        try (var conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1")) {

            DatabaseUtils.execute(conn, "create table test (id INTEGER, text_column varchar(20) default 'abc',  PRIMARY KEY (id),\n)");

            DatabaseUtils.execute(conn, "insert into test (id , text_column) values (1,'1 Some test data')");
            DatabaseUtils.execute(conn, "insert into test (id , text_column) values (2,'2 Some test data')");

            OrmRecordLoader<TestRecord> loader = new OrmRecordLoader<TestRecord>(conn, "Select id as \"id\", text_column as \"textColumn\" from test where id = ?", TestRecord.class);

            assertEquals(new TestRecord(1,"1 Some test data"), loader.getObject(1));
            assertEquals(new TestRecord(2,"2 Some test data"), loader.getObject(2));
        }
    }


}

