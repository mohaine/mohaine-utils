package com.mohaine.db.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.orm.engine.Column;
import com.mohaine.db.orm.engine.FieldDefiniation;
import com.mohaine.db.orm.engine.KeyedObjectTableMapping;
import com.mohaine.db.orm.engine.LoadFieldDefinition;
import com.mohaine.db.orm.engine.MappingCache;
import com.mohaine.db.orm.engine.ObjectTableMapping;
import com.mohaine.db.orm.engine.SqlDelete;
import com.mohaine.db.orm.engine.SqlGenerator;
import com.mohaine.db.orm.engine.SqlInsert;
import com.mohaine.db.orm.engine.SqlSet;
import com.mohaine.db.orm.engine.SqlTable;
import com.mohaine.db.orm.engine.Update;
import com.mohaine.db.orm.engine.Where;
import com.mohaine.util.StringUtils;

public class OrmHandler<T> {

    private final Connection connection;
    private final ObjectTableMapping mapping;

    private String tableName = null;
    private static SqlGenerator generator = new SqlGenerator();
    private boolean updateDirty = true;
    private String tablePrefix = null;

    public OrmHandler(Connection connection, ObjectTableMapping mapping) {
        super();
        this.tableName = mapping.getTableName();
        this.connection = connection;
        this.mapping = mapping;
    }

    public OrmHandler(Connection conn, Class<T> mappingClass) {
        this(conn, MappingCache.getMapping(mappingClass));
    }

    public List<T> saveObjects(List<T> objects) throws SQLException {
        return updateOrInsertObjects(objects);
    }

    public List<T> updateOrInsertObjects(List<T> saveObjects) throws SQLException {
        return updateOrInsertObjects(saveObjects, false);
    }

    public List<T> updateOrInsertObjects(List<T> saveObjects, boolean checkDirty) throws SQLException {

        if (!(mapping instanceof KeyedObjectTableMapping)) {
            throw new RuntimeException("Can not update non keyed mappings");
        }

        KeyedObjectTableMapping keyedMapping = (KeyedObjectTableMapping) mapping;
        List<LoadFieldDefinition> fields = keyedMapping.getFields();
        List<FieldDefiniation> keys = keyedMapping.getKeys();

        List<T> insertObjects = new ArrayList<T>();

        PreparedStatement updateStatement = null;
        try {

            updateStatement = prepareUpdateStatement(fields, keys, keyedMapping.getParentKey());

            for (Iterator<T> iter = saveObjects.iterator(); iter.hasNext(); ) {
                T saveObject = iter.next();

                if (checkDirty && saveObject instanceof Dirty) {
                    if (!((Dirty) saveObject).isDirty()) {
                        continue;
                    }
                }

                boolean skipUpdate = false;
                if (keys != null) {

                    for (FieldDefiniation key : keys) {
                        if (key.getValue(saveObject) == null) {
                            skipUpdate = true;
                            break;
                        }
                    }
                }

                int updateCount = 0;
                if (!skipUpdate) {
                    bindUpdateStatement(fields, keys, keyedMapping.getParentKey(), updateStatement, saveObject);
                    updateCount = updateStatement.executeUpdate();
                }

                if (updateCount == 0) {
                    // Object was not updated, we need to insert
                    insertObjects.add(saveObject);
                }

                // If a dirty type object reset as not dirty
                if (updateDirty && saveObject instanceof Dirty) {
                    ((Dirty) saveObject).resetDirty();
                }
            }
        } finally {
            DatabaseUtils.close(updateStatement);
        }
        insertObjects(insertObjects);
        return saveObjects;
    }

    public T saveObject(T saveObject) throws SQLException {
        ArrayList<T> list = new ArrayList<T>();
        list.add(saveObject);
        return saveObjects(list).get(0);
    }

    public T insertObject(T saveObject) throws SQLException {
        ArrayList<T> list = new ArrayList<T>();
        list.add(saveObject);
        return insertObjects(list).get(0);
    }

    public T updateObject(T saveObject) throws SQLException {
        ArrayList<T> list = new ArrayList<T>();
        list.add(saveObject);
        return updateObjects(list).get(0);
    }

    public void removeObject(T saveObject) throws SQLException {
        ArrayList<T> list = new ArrayList<T>();
        list.add(saveObject);
        removeObjects(list);
    }

    public void removeObjects(Collection<T> saveObjects) throws SQLException {
        if (!(mapping instanceof KeyedObjectTableMapping)) {
            throw new RuntimeException("Can not delete non keyed mappings");
        }

        KeyedObjectTableMapping keyedMapping = (KeyedObjectTableMapping) mapping;
        List<LoadFieldDefinition> fields = keyedMapping.getFields();
        List<FieldDefiniation> keys = keyedMapping.getKeys();

        PreparedStatement updateStatement = null;
        try {
            updateStatement = prepareRemoveStatement(fields, keys);
            for (Iterator<T> iter = saveObjects.iterator(); iter.hasNext(); ) {
                T saveObject = iter.next();
                bindRemoveStatement(fields, keys, updateStatement, saveObject);
                updateStatement.execute();
            }
        } finally {
            DatabaseUtils.close(updateStatement);
        }

    }

    private List<T> updateObjects(List<T> saveObjects) throws SQLException {

        if (!(mapping instanceof KeyedObjectTableMapping)) {
            throw new RuntimeException("Can not update non keyed mappings");
        }

        KeyedObjectTableMapping keyedMapping = (KeyedObjectTableMapping) mapping;
        List<LoadFieldDefinition> fields = keyedMapping.getFields();
        List<FieldDefiniation> keys = keyedMapping.getKeys();

        PreparedStatement updateStatement = null;
        try {

            updateStatement = prepareUpdateStatement(fields, keys, keyedMapping.getParentKey());

            int batchCount = 0;
            for (Iterator<T> iter = saveObjects.iterator(); iter.hasNext(); ) {
                Object saveObject = iter.next();
                bindUpdateStatement(fields, keys, keyedMapping.getParentKey(), updateStatement, saveObject);
                // If a dirty type object reset as not dirty
                if (updateDirty && saveObject instanceof Dirty) {
                    ((Dirty) saveObject).resetDirty();
                }
                batchCount++;

                updateStatement.addBatch();

                if (batchCount > 500) {
                    batchCount = 0;
                    updateStatement.executeBatch();
                }
            }
            if (batchCount > 0) {
                updateStatement.executeBatch();
            }
        } finally {
            DatabaseUtils.close(updateStatement);
        }
        return saveObjects;
    }

    private void bindRemoveStatement(List<LoadFieldDefinition> fields, List<FieldDefiniation> keys, PreparedStatement updateStatement, Object saveObject) throws SQLException {
        for (int i = 0, size = keys.size(); i < size; i++) {
            FieldDefiniation field = keys.get(i);
            Object value = field.getValue(saveObject);
            setObject(updateStatement, i + 1, value);
        }
    }

    private void bindUpdateStatement(List<LoadFieldDefinition> fields, List<FieldDefiniation> keys, FieldDefiniation parentKey, PreparedStatement updateStatement, Object saveObject)
        throws SQLException {
        int count = 1;
        for (int i = 0, size = fields.size(); i < size; i++) {
            LoadFieldDefinition loadField = fields.get(i);
            if (loadField instanceof FieldDefiniation) {
                FieldDefiniation field = (FieldDefiniation) loadField;
                Object value = field.getValue(saveObject);
                setObject(updateStatement, count++, value);
            }
        }

        for (int i = 0, size = keys.size(); i < size; i++) {
            FieldDefiniation field = keys.get(i);
            Object value = field.getValue(saveObject);
            if (value != null) {
                setObject(updateStatement, count++, value);
            } else {
                throw new RuntimeException("Null KEY for field " + field.getColumnName() + ".  This doesn't currently work");
            }
        }
        if (parentKey != null) {
            FieldDefiniation field = parentKey;
            Object value = field.getValue(saveObject);
            if (value != null) {
                setObject(updateStatement, count++, value);
            } else {
                throw new RuntimeException("Parent key null KEY for field " + field.getColumnName() + ".  This doesn't work");
            }
        }

    }

    private PreparedStatement prepareRemoveStatement(List<LoadFieldDefinition> fields, List<FieldDefiniation> keys) throws SQLException {
        PreparedStatement updateStatement;
        SqlDelete queryInfo = new SqlDelete();

        List<SqlTable> tables = queryInfo.getTables();
        List<Where> wheres = queryInfo.getWheres();

        SqlTable mainTable = new SqlTable(getCUDTableName());
        tables.add(mainTable);

        for (int i = 0, size = keys.size(); i < size; i++) {
            FieldDefiniation field = keys.get(i);
            Column column = new Column(field.getColumnName());
            Where where = new Where(column);
            wheres.add(where);
        }

        String sql = generator.generateSql(queryInfo);

        updateStatement = connection.prepareStatement(sql);
        return updateStatement;
    }

    private PreparedStatement prepareUpdateStatement(List<LoadFieldDefinition> fields, List<FieldDefiniation> keys, FieldDefiniation parentKey) throws SQLException {
        PreparedStatement updateStatement;
        Update queryInfo = new Update();

        List<SqlTable> tables = queryInfo.getTables();
        List<SqlSet> sets = queryInfo.getSets();
        List<Where> wheres = queryInfo.getWheres();

        SqlTable mainTable = new SqlTable(getCUDTableName());
        tables.add(mainTable);

        for (int i = 0, size = fields.size(); i < size; i++) {
            LoadFieldDefinition loadField = fields.get(i);
            if (loadField instanceof FieldDefiniation) {
                FieldDefiniation field = (FieldDefiniation) loadField;
                Column column = new Column(field.getColumnName());
                SqlSet set = new SqlSet(column, field.getModifyBind());
                sets.add(set);
            }
        }

        for (int i = 0, size = keys.size(); i < size; i++) {
            FieldDefiniation field = keys.get(i);
            Column column = new Column(field.getColumnName());
            Where where = new Where(column);
            wheres.add(where);
        }

        if (parentKey != null) {
            Column column = new Column(parentKey.getColumnName());
            Where where = new Where(column);
            wheres.add(where);
        }

        String sql = generator.generateSql(queryInfo);

        updateStatement = connection.prepareStatement(sql);
        return updateStatement;
    }

    public List<T> insertObjects(List<T> saveObjects) throws SQLException {
        boolean batchInserts = saveObjects.size() != 1;
        FieldDefiniation singleKey = null;
        if (mapping instanceof KeyedObjectTableMapping) {
            KeyedObjectTableMapping keyedMapping = (KeyedObjectTableMapping) mapping;
            List<FieldDefiniation> keys = keyedMapping.getKeys();
            if (keys.size() == 1) {
                singleKey = keys.get(0);
                if (singleKey.isPostSelectKey()) {
                    batchInserts = false;
                }
            }
        }
        PreparedStatement statement = null;

        try {
            List<LoadFieldDefinition> fields = mapping.getFields();

            statement = prepareInsertStatement(fields);

            int batchCount = 0;
            for (Iterator<T> iter = saveObjects.iterator(); iter.hasNext(); ) {
                Object saveObject = iter.next();

                if (singleKey != null && !singleKey.isPostSelectKey() && !StringUtils.hasLength(StringUtils.valueOf(singleKey.getValue(saveObject)))) {
                    singleKey.setValue(saveObject, mapping.generateKey());

                }

                bindInsertStatement(statement, fields, saveObject);
                // If a dirty type object reset as not dirty
                if (updateDirty && saveObject instanceof Dirty) {
                    ((Dirty) saveObject).resetDirty();
                }

                if (!batchInserts) {
                    statement.execute();

                    if (singleKey != null && singleKey.isPostSelectKey() && !StringUtils.hasLength(StringUtils.valueOf(singleKey.getValue(saveObject)))) {
                        singleKey.setValue(saveObject, mapping.generateKey());
                    }
                } else {
                    batchCount++;
                    statement.addBatch();
                    if (batchCount > 500) {
                        batchCount = 0;
                        statement.executeBatch();
                    }
                }
            }
            if (batchCount > 0) {
                statement.executeBatch();
            }

        } finally {
            DatabaseUtils.close(statement);
        }
        return saveObjects;
    }


    private void bindInsertStatement(PreparedStatement statement, List<LoadFieldDefinition> fields, Object saveObject) throws SQLException {
        List<Object> binds = new ArrayList<Object>();
        for (int i = 0, size = fields.size(); i < size; i++) {
            LoadFieldDefinition loadField = fields.get(i);
            if (loadField instanceof FieldDefiniation) {
                FieldDefiniation field = (FieldDefiniation) loadField;
                Object value = field.getValue(saveObject);
                binds.add(value);
            }
        }

        for (int i = 0, size = binds.size(); i < size; i++) {
            Object value = binds.get(i);
            setObject(statement, i + 1, value);
        }
    }

    private PreparedStatement prepareInsertStatement(List<LoadFieldDefinition> fields) throws SQLException {
        PreparedStatement statement;
        SqlInsert queryInfo = new SqlInsert();
        List<SqlTable> tables = queryInfo.getTables();
        List<SqlSet> sets = queryInfo.getValues();
        SqlTable mainTable = new SqlTable(getCUDTableName());
        tables.add(mainTable);

        for (int i = 0, size = fields.size(); i < size; i++) {
            LoadFieldDefinition loadField = fields.get(i);
            if (loadField instanceof FieldDefiniation) {
                FieldDefiniation field = (FieldDefiniation) loadField;
                Column column = new Column(field.getColumnName());
                SqlSet set = new SqlSet(column, field.getModifyBind());
                sets.add(set);
            }
        }

        String sql = generator.generateSql(queryInfo);

        statement = connection.prepareStatement(sql);
        return statement;
    }

    private String getCUDTableName() {
        if (tablePrefix != null) {
            return tablePrefix + tableName;
        }

        return tableName;
    }

    private void setObject(PreparedStatement statement, int i, Object value) throws SQLException {
        Object bindObject = SqlGenerator.getBindObject(value);
        if (bindObject == null) {
            statement.setNull(i, Types.OTHER);
        } else if (bindObject instanceof UUID) {
            statement.setObject(i, bindObject, Types.OTHER);
        } else {
            statement.setObject(i, bindObject);
        }
    }

    public void updateOrInsertObject(T object) throws SQLException {
        ArrayList<T> arrayList = new ArrayList<T>();
        arrayList.add(object);
        updateOrInsertObjects(arrayList);
    }

    public boolean isUpdateDirty() {
        return updateDirty;
    }

    public void setUpdateDirty(boolean updateDirty) {
        this.updateDirty = updateDirty;
    }

    public void deleteMissingChildren(Object parentKeyValue, List<T> allExistingChildren) throws SQLException {
        deleteMissingChildren(parentKeyValue, allExistingChildren, null, null);
    }

    public void deleteMissingChildren(Object parentKeyValue, List<T> allExistingChildren, String extraSql, List<?> extraBinds) throws SQLException {

        if (!(mapping instanceof KeyedObjectTableMapping)) {
            throw new RuntimeException("Can not update non keyed mappings");
        }

        KeyedObjectTableMapping keyedMapping = (KeyedObjectTableMapping) mapping;
        List<FieldDefiniation> keys = keyedMapping.getKeys();

        List<Object> binds = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ");
        sql.append(getCUDTableName());
        sql.append(" WHERE ");

        FieldDefiniation parentKeyField = keyedMapping.getParentKey();
        if (parentKeyField == null) {
            throw new RuntimeException("no parent key");
        }
        sql.append(parentKeyField.getColumnName());
        sql.append(" = ?");
        binds.add(parentKeyValue);

        if (allExistingChildren.size() > 0) {
            sql.append(" AND ((");
            int keyCount = 0;
            for (FieldDefiniation key : keys) {
                if (keyCount > 0) {
                    sql.append(',');
                }
                sql.append(key.getColumnName());
                keyCount++;
            }
            sql.append(") not in (");

            int childCount = 0;
            for (Object child : allExistingChildren) {

                if (childCount > 0) {
                    sql.append(',');
                }
                sql.append("(");
                keyCount = 0;
                for (FieldDefiniation key : keys) {

                    if (keyCount > 0) {
                        sql.append(',');
                    }
                    sql.append('?');
                    binds.add(key.getValue(child));
                    keyCount++;
                }
                sql.append(")");

                childCount++;
            }
            sql.append("))");
        }

        if (extraSql != null) {
            sql.append(extraSql);
            if (extraBinds != null) {
                binds.addAll(extraBinds);
            }
        }
        DatabaseUtils.execute(connection, sql.toString(), binds.toArray());
    }

    public void saveAllChildren(Object parentKeyValue, List<T> allExistingChildren) throws SQLException {
        if (!(mapping instanceof KeyedObjectTableMapping)) {
            throw new RuntimeException("Can not update non keyed mappings");
        }
        KeyedObjectTableMapping keyedMapping = (KeyedObjectTableMapping) mapping;

        FieldDefiniation parentKeyField = keyedMapping.getParentKey();
        if (parentKeyField == null) {
            throw new RuntimeException("no parent key defined in mapping");
        }

        for (Object child : allExistingChildren) {
            parentKeyField.setValue(child, parentKeyValue);
        }

        updateOrInsertObjects(allExistingChildren);
        deleteMissingChildren(parentKeyValue, allExistingChildren);
    }

    public void setTableName(String string) {
        this.tableName = string;
    }

    /**
     * This is a quick and dirty for the ProcessSqlGenerator. Probably need a better solution.....
     *
     * @param string
     */
    public void setTablePrefix(String string) {
        this.tablePrefix = string;
    }
}