package com.mohaine.db;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SqlBinder_UT {

    @Test
    public void testBasics() {
        Map<String, Object> binds = new HashMap<>();

        binds.put("test1", "TEST1");
        binds.put("test2", "TEST2");
        assertEquals(new SqlBinder.SqlBinds("select * from abc", new ArrayList<Object>()), SqlBinder.parseBinds("select * from abc", binds));

        assertEquals(new SqlBinder.SqlBinds("select * from abc where c1 = ?", Arrays.asList("TEST1"))
                , SqlBinder.parseBinds("select * from abc where c1 = ${test1}", binds));

        assertEquals(new SqlBinder.SqlBinds("select * from abc where c1 = ? and c2 = ? order by 1", Arrays.asList("TEST1", "TEST2"))
                , SqlBinder.parseBinds("select * from abc where c1 = ${test1} and c2 = ${test2} order by 1", binds));

        assertEquals(new SqlBinder.SqlBinds("? ? ?", Arrays.asList("TEST1", "TEST2", "TEST1"))
                , SqlBinder.parseBinds("${test1} ${test2} ${test1}", binds));

        assertEquals(new SqlBinder.SqlBinds("A? ? ?Z", Arrays.asList("TEST1", "TEST2", "TEST1"))
                , SqlBinder.parseBinds("A${test1} ${test2} ${test1}Z", binds));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            SqlBinder.parseBinds("${dne}", binds);
        });

        assertEquals("Failed to find mapping for 'dne'", exception.getMessage());


        assertEquals(new SqlBinder.SqlBinds("", Arrays.asList())
                , SqlBinder.parseBinds("", binds));
        assertEquals(new SqlBinder.SqlBinds("${NotAMatchDueTo*}", Arrays.asList())
                , SqlBinder.parseBinds("${NotAMatchDueTo*}", binds));
    }


}
