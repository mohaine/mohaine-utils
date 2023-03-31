package com.mohaine.util;

import com.mohaine.json.*;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReflectionObjectBuilder_UT {


    @Test
    public void testRecordDefault() throws Exception {
        record TestRecord(String id, String textColumn) {
        };

        var builder = new ReflectionObjectBuilder<TestRecord>(TestRecord.class);

        var fields = new HashMap<String, Object>();

        fields.put("id", "ID1");
        fields.put("textColumn", "textColumn1");
        fields.put("unknownColumn", "unknownColumn1");

        var results = builder.createNewObject(fields);

        assertEquals(new TestRecord("ID1", "textColumn1"), results.obj());
        assertEquals(1, results.unhandledNames().size());
        assertEquals(true, results.unhandledNames().contains("unknownColumn"));
    }

//    @Test
//    public void testRecordOtherConstructor() throws Exception {
//        record TestRecord(String id, String textColumn) {
//            public TestRecord(String textColumn){
//                this("DEFAULT_ID", textColumn);
//            }
//        };
//
//        var builder = new ReflectionObjectBuilder<TestRecord>(TestRecord.class);
//
//        var fields = new HashMap<String, Object>();
//
//        fields.put("textColumn", "textColumn1");
//        fields.put("unknownColumn", "unknownColumn1");
//
//        var results = builder.createNewObject(fields);
//
//        assertEquals(new TestRecord("DEFAULT_ID", "textColumn1"), results.obj());
//        assertEquals(1, results.unhandledNames().size());
//        assertEquals(true, results.unhandledNames().contains("unknownColumn"));
//    }
}
