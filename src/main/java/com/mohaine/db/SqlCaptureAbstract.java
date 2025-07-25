package com.mohaine.db;

import java.text.SimpleDateFormat;

public abstract class SqlCaptureAbstract implements SqlCapture {
    @Override
    public void beforeRun(Object sql) {

    }

    @Override
    public void afterRun(Object sql, long startTime) {

    }

    @Override
    public void afterError(Object sql) {

    }

    @Override
    public void preBindSql(String sql) {

    }

    @Override
    public String getBindSqlString(Object bindObject) {
        StringBuilder sb = new StringBuilder();
        if (bindObject instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ssaa");
            sb.append("to_date('");
            sb.append(sdf.format((java.util.Date) bindObject));
            sb.append("', 'yyyy/mm/dd hh:mi:ssam')");
        } else if (bindObject instanceof Number) {
            sb.append(bindObject);
        } else {
            sb.append('\'');

            String bindStr = bindObject.toString();
            int length = bindStr.length();
            for (int i = 0; i < length; i++) {
                char charAt = bindStr.charAt(i);
                switch (charAt) {
                    case '\'':
                        sb.append("''");
                        break;
                    // case '\r':
                    // sb.append("\\r");
                    // break;
                    // case '\n':
                    // sb.append("\\n");
                    // break;
                    default:
                        sb.append(charAt);
                }
            }

            sb.append('\'');
        }
        return sb.toString();

    }

}
