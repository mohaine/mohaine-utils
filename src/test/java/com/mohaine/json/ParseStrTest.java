package com.mohaine.json;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by mcgraes on 6/6/17.
 */
public class ParseStrTest extends TestCase {
    @Test
    public void testParse() {
        JsonDecoder dc = new JsonDecoder("{\"fieldInt\":1,\"fieldFloat\":1.2\"fieldStr\":\"Str\"}");
        JsonUnknownObject o = (JsonUnknownObject) dc.parseJson();
        assertEquals(1l, o.getProperty("fieldInt"));
        assertEquals(1.2, o.getProperty("fieldFloat"));
        assertEquals("Str", o.getProperty("fieldStr"));
    }
}
