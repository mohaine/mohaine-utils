package com.mohaine.db;


import com.sun.source.tree.PatternTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SqlBinder {
    public record SqlBinds(String sql, List<Object> binds) {
    }


    public static SqlBinds parseBinds(String sql, Map<String, Object> binds) {

        var jdbcSql = new StringBuilder();
        var index = 0;

        var p = Pattern.compile("\\$\\{(\\w+)\\}");

        var bindsByName = new ArrayList<String>();
        var matcher = p.matcher(sql);
        while (matcher.find()) {
            jdbcSql.append(sql.substring(index, matcher.start()));
            jdbcSql.append("?");
            index = matcher.end();
            bindsByName.add(matcher.group(1));
        }
        if (index < sql.length()) {
            jdbcSql.append(sql.substring(index));
        }


        var jdbcBinds = new ArrayList<Object>();
        for (var bindByName : bindsByName) {
            if (!binds.containsKey(bindByName)) {
                throw new RuntimeException("Failed to find mapping for '" + bindByName + "'");
            }

            jdbcBinds.add(binds.get(bindByName));
        }


        return new SqlBinds(jdbcSql.toString(), jdbcBinds);
    }


}
