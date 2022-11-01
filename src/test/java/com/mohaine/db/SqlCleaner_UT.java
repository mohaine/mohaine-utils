package com.mohaine.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlCleaner_UT {
    @Test
    public void testBasics() {
        assertEquals("select * from abc", SqlCleaner.cleanSql("select * from abc"));
        assertEquals("select * from abc", SqlCleaner.cleanSql("   select \n* \nfrom\rabc   "));
    }


    @Test
    public void testWithSingleLineComment() {
        assertEquals("select * /* test comment */from abc", SqlCleaner.cleanSql("select * -- test comment \n from abc"));
        assertEquals("select * /* test comment */from abc", SqlCleaner.cleanSql("select * // test comment \n from abc"));
        assertEquals("select '-- not a comment' from abc", SqlCleaner.cleanSql("select '-- not a comment' \n from abc"));
        assertEquals("select '// not a comment' from abc", SqlCleaner.cleanSql("select '// not a comment' \n from abc"));
    }
}
