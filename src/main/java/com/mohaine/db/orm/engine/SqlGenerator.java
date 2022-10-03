/*
 * Created on Mar 22, 2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mohaine.db.orm.engine;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * @author graessle
 */
public class SqlGenerator {

    public String generateSql(Statement statementInfomartion) {
        StringBuffer buffer = new StringBuffer(256);
        if (statementInfomartion instanceof SqlInsert) {
            buffer.append("INSERT INTO ");
            SqlTable table = (SqlTable) statementInfomartion.getTables().get(0);
            buffer.append(table.getTableName());
            buffer.append(' ');
            appendValueNames(buffer, (SqlInsert) statementInfomartion);
            buffer.append(' ');
            appendValues(buffer, (SqlInsert) statementInfomartion);
        } else if (statementInfomartion instanceof Update) {
            buffer.append("update ");
            SqlTable table = (SqlTable) statementInfomartion.getTables().get(0);
            buffer.append(table.getTableName());
            buffer.append(" set ");

            appendSets(buffer, (Update) statementInfomartion);
            buffer.append(" where ");
            appendWheres(buffer, (Update) statementInfomartion);
        } else if (statementInfomartion instanceof SqlDelete) {
            buffer.append("delete from  ");
            SqlTable table = (SqlTable) statementInfomartion.getTables().get(0);
            buffer.append(table.getTableName());
            buffer.append(" where ");
            appendWheres(buffer, (SqlDelete) statementInfomartion);
        } else if (statementInfomartion instanceof Sequence) {
            buffer.append("SELECT ");
            SqlTable table = (SqlTable) statementInfomartion.getTables().get(0);
            buffer.append(table.getTableName());
            buffer.append(".nextval from dual");
        }

        return buffer.toString();
    }

    private void appendWheres(StringBuffer buffer, SqlDelete update) {
        List<Where> wheres = update.getWheres();
        if (wheres != null) {
            for (int i = 0, size = wheres.size(); i < size; i++) {
                if (i > 0) {
                    buffer.append(" and ");
                }
                Where where = (Where) wheres.get(i);

                buffer.append(where.getColumn().getColumnName() + " =  ? ");
            }
        }
    }

    private void appendWheres(StringBuffer buffer, Update update) {
        List<Where> wheres = update.getWheres();
        if (wheres != null) {
            for (int i = 0, size = wheres.size(); i < size; i++) {
                if (i > 0) {
                    buffer.append(" and ");
                }
                Where where = (Where) wheres.get(i);

                buffer.append(where.getColumn().getColumnName() + " =  ? ");
            }
        }
    }

    public void appendValues(StringBuffer buffer, SqlInsert queryInfomartion) {
        List<SqlSet> sets = queryInfomartion.getValues();
        if (sets != null) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                if (i > 0) {
                    buffer.append(", ");
                } else {
                    buffer.append("VALUES ( ");
                }
                SqlSet set = (SqlSet) sets.get(i);
                String bind = set.getBind();
                if (bind == null || bind.length() == 0) {
                    bind = "?";
                }
                buffer.append(bind);
            }
            buffer.append(") ");
        }
    }

    public void appendSets(StringBuffer buffer, Update queryInfomartion) {
        List<SqlSet> sets = queryInfomartion.getSets();
        if (sets != null) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                SqlSet set = (SqlSet) sets.get(i);

                String bind = set.getBind();
                if (bind == null || bind.length() == 0) {
                    bind = "?";
                }

                buffer.append(set.getColumn().getColumnName() + " = " + bind);
            }
        }
    }

    public void appendValueNames(StringBuffer buffer, SqlInsert queryInfomartion) {
        List<SqlSet> sets = queryInfomartion.getValues();
        if (sets != null) {
            for (int i = 0, size = sets.size(); i < size; i++) {
                if (i > 0) {
                    buffer.append(", ");
                } else {
                    buffer.append('(');
                }
                SqlSet set = (SqlSet) sets.get(i);
                appendValue(queryInfomartion, buffer, set.getColumn());
            }
            buffer.append(") ");
        }
    }

    public void appendFroms(StringBuffer buffer, Statement queryInfomartion) {
        List<SqlTable> tables = queryInfomartion.getTables();
        buffer.append("FROM ");
        for (int i = 0, size = tables.size(); i < size; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            SqlTable table = (SqlTable) tables.get(i);
            buffer.append(table.getTableName());
            buffer.append(" ");
            buffer.append(table.getAlias());
        }
        buffer.append(" ");
    }

    public void appendValue(Statement queryInfomartion, StringBuffer buffer, Column column) {
        buffer.append(column.getColumnName());
    }

    public static Object getBindObject(Object value) {
        if ((value instanceof java.util.Date) && !(value instanceof java.sql.Date)) {
            java.util.Date utilDate = (java.util.Date) value;
            value = new Timestamp(utilDate.getTime());
        } else if (value instanceof UUID) {
            value = value.toString();
        }

        return value;
    }
}
