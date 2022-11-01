package com.mohaine.db;

public class SqlCleaner {

    public static String cleanSql(String sql) {
        StringBuffer sb = new StringBuffer(sql.length());
        // Clean up the SQL some
        char[] sqlChars = sql.toCharArray();

        boolean inSingleLineComment = false;
        boolean lastWhite = true;
        boolean inString = false;

        for (int i = 0; i < sqlChars.length; i++) {
            char current = sqlChars[i];

            if (current == '\n' || current == '\r') {
                if (inSingleLineComment) {
                    sb.append("*/");
                }
                inSingleLineComment = false;
            }

            // Strip all single line comments since we are going single line
            else if (i < sqlChars.length + 1) {
                if (current == '\'') {
                    if (!inSingleLineComment) {
                        inString = !inString;
                    }
                }
                if (current == '/'&& !inString) {
                    if (sqlChars[i + 1] == '/') {
                        inSingleLineComment = true;
                        sqlChars[i + 1] = '*';
                    }
                }
                if (current == '-' && !inString) {
                    if (sqlChars[i + 1] == '-') {
                        inSingleLineComment = true;
                        current = '/';
                        sqlChars[i + 1] = '*';
                    }
                }
            }

            boolean isWhite = Character.isWhitespace(current);
            if (isWhite && lastWhite) {
                continue;
            }
            if (isWhite) {
                current = ' ';
            }
            lastWhite = isWhite;

            sb.append(current);
        }

        return sb.toString().trim();
    }
}
