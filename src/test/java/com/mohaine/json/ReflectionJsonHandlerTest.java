package com.mohaine.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReflectionJsonHandlerTest {

    public record TestRecord(String stringField) {
    }


    public record TestRecordWithList(String field, List<TestRecord> records) {
    }


    public static class TestClass {
        private String stringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }

    @Test
    public void testTestClass() throws Exception {
        var handler = ReflectionJsonHandler.build(TestClass.class);
        JsonConverterConfig config = new JsonConverterConfig();
        config.addHandler(handler);

        JsonEncoder encoder = new JsonEncoder(config);

        StringBuilder sb = new StringBuilder();

        var value = new TestClass();
        value.setStringField("Some Random Value");
        encoder.appendObject(sb, value);

        System.out.println(sb.toString());
        JsonDecoder decoder = new JsonDecoder(config, sb.toString());
        var valueBack = decoder.parseJson(TestClass.class);
        assertNotNull(valueBack);

        assertEquals(value.getStringField(), valueBack.getStringField());
    }

    @Test
    public void testTestRecord() throws Exception {
        var handler = ReflectionJsonHandler.build(TestRecord.class);
        JsonConverterConfig config = new JsonConverterConfig();
        config.addHandler(handler);

        JsonEncoder encoder = new JsonEncoder(config);

        StringBuilder sb = new StringBuilder();

        var value = new TestRecord("Some Random Value");
        encoder.appendObject(sb, value);

        System.out.println(sb.toString());
        JsonDecoder decoder = new JsonDecoder(config, sb.toString());
        var valueBack = decoder.parseJson(TestRecord.class);
        assertNotNull(valueBack);

        assertEquals(value.stringField, valueBack.stringField);
    }

    @Test
    public void testTestList() throws Exception {
        JsonConverterConfig config = new JsonConverterConfig();
        config.addHandler(ReflectionJsonHandler.build(TestRecordWithList.class));
        config.addHandler(ReflectionJsonHandler.build(TestRecord.class));

        JsonEncoder encoder = new JsonEncoder(config);

        StringBuilder sb = new StringBuilder();

        var list = new ArrayList<TestRecord>();
        list.add(new TestRecord("Child 1"));
        list.add(new TestRecord("Child 2"));
        var value = new TestRecordWithList("Some Random Value", list);
        encoder.appendObject(sb, value);

        System.out.println("JSON: " + sb.toString());
        JsonDecoder decoder = new JsonDecoder(config, sb.toString());
        var valueBack = decoder.parseJson(TestRecordWithList.class);
        assertNotNull(valueBack);

        assertEquals(value.field, valueBack.field);
        assertEquals(value.records, valueBack.records);
    }

    @Test
    public void testEasyButton() throws Exception {


        var list = new ArrayList<TestRecord>();
        list.add(new TestRecord("Child 1"));
        list.add(new TestRecord("Child 2"));
        var value = new TestRecordWithList("Some Random Value", list);


        var json = Json.encode(value);
        System.out.println("json: " + json);
        var valueBack = Json.decode(json, TestRecordWithList.class);
        assertNotNull(valueBack);

        assertEquals(value.field, valueBack.field);
        assertEquals(value.records, valueBack.records);
    }

}
