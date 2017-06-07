package com.mohaine.json;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.StringReader;

/**
 * Created by mcgraes on 6/6/17.
 */
public class ParseReaderTest extends TestCase {
    @Test
    public void testParseNoWhiteSpace() throws Exception {
        JsonDecoderReader dc = new JsonDecoderReader(new StringReader("{\"fieldInt\":1,\"fieldFloat\":1.2\"fieldStr\":\"Str\",\"fieldNull\":null,\"fieldBoolT\":true\"fieldBoolF\":false}"));
        JsonUnknownObject o = (JsonUnknownObject) dc.parseJson();
        assertEquals(1l,((Long) o.getProperty("fieldInt")).longValue());
        assertEquals(1.2, o.getProperty("fieldFloat"));
        assertEquals("Str", o.getProperty("fieldStr"));
        assertEquals(null, o.getProperty("fieldNull"));
        assertEquals(true, ((Boolean)o.getProperty("fieldBoolT")).booleanValue());
        assertEquals(false,((Boolean) o.getProperty("fieldBoolF")).booleanValue());
    }


    @Test
    public void testParseWhiteSpace() throws Exception {
        JsonDecoderReader dc = new JsonDecoderReader(new StringReader("{  \"fieldInt\" : 1, \"fieldFloat\":1.2\"fieldStr\"   :  \"Str\"   }"));
        JsonUnknownObject o = (JsonUnknownObject) dc.parseJson();
        assertEquals(1l, ((Long)o.getProperty("fieldInt")).longValue());
        assertEquals(1.2, o.getProperty("fieldFloat"));
        assertEquals("Str", o.getProperty("fieldStr"));
    }

    @Test
    public void testParseNested() throws Exception {
        String objString = " {  \"fieldInt\"    : 1,\"fieldFloat\":1.2\"fieldStr\":\"Str\",\"fieldNull\":null,\"fieldBoolT\":true\"fieldBoolF\":false}";

        JsonDecoderReader dc = new JsonDecoderReader(new StringReader("{\"obj\"  :   "+objString+"}"));

        JsonUnknownObject p = (JsonUnknownObject) dc.parseJson();
        JsonUnknownObject o = (JsonUnknownObject) p.getProperty("obj");
        assertEquals(1l,((Long) o.getProperty("fieldInt")).longValue());
        assertEquals(1.2, o.getProperty("fieldFloat"));
        assertEquals("Str", o.getProperty("fieldStr"));
    }
}
