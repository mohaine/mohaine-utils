package com.mohaine.db.orm;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.orm.engine.LoadFieldDefinition;
import com.mohaine.db.orm.engine.MappingCache;
import com.mohaine.db.orm.engine.ObjectTableMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OrmLoader<E> {
    private static final int FETCH_SIZE = 1000;
    private final Connection connection;
    private final String sql;
    private final List<Object> binds = new ArrayList<Object>();
    private ObjectTableMapping mapping = null;
    private PreparedStatement preparedStatment = null;
    private boolean ignoreDirty = false;

    private int[] resultSetColumnIndexes = null;
    private int[] resultSetColumnTypes = null;

    private ResultSet resultSet = null;

    public OrmLoader(Connection connection, String sql, ObjectTableMapping mapping) {
        super();
        this.sql = sql;
        this.connection = connection;
        this.mapping = mapping;
    }

    public OrmLoader(Connection connection, String sql, Class<E> mappingClass) {
        this(connection, sql, MappingCache.getMapping(mappingClass));
    }

    public boolean isIgnoreDirty() {
        return ignoreDirty;
    }

    public void setIgnoreDirty(boolean ignoreDirty) {
        this.ignoreDirty = ignoreDirty;
    }

    public void addBinds(List<?> binds) {
        this.binds.addAll(binds);
    }

    public void addBind(Object bind) {
        binds.add(bind);
    }

    public void close() {
        DatabaseUtils.close(resultSet);
        DatabaseUtils.close(preparedStatment);
        resultSetColumnIndexes = null;
        resultSetColumnTypes = null;
        resultSet = null;
        preparedStatment = null;
    }

    public void clearBinds() {
        binds.clear();
    }

    public List<E> getObjectsNoClose() throws SQLException {
        return getObjectsArrayListNoClose();
    }

    public ArrayList<E> getObjectsArrayListNoClose() throws SQLException {
        ResultSet resultSet = null;

        try {
            ArrayList<E> results = new ArrayList<E>();
            if (preparedStatment == null) {
                preparedStatment = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            bindStatement();
            resultSet = preparedStatment.executeQuery();
            resultSet.setFetchDirection(ResultSet.FETCH_FORWARD);
            resultSet.setFetchSize(FETCH_SIZE);

            // populate results
            while (resultSet.next()) {
                results.add(getObjectFromResultSet(resultSet, 0));
            }
            return results;
        } finally {
            DatabaseUtils.close(resultSet);
        }
    }

    public E getObjectNoClose() throws SQLException {
        if (preparedStatment == null) {
            preparedStatment = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            bindStatement();
            resultSet = preparedStatment.executeQuery();
            resultSet.setFetchSize(1);
        }
        // populate results
        if (resultSet.next()) {
            return getObjectFromResultSet(resultSet, 0);
        }
        return null;
    }

    public E getObjectNewQueryNoCloseStatement() throws SQLException {
        ResultSet resultSet = null;
        try {
            if (preparedStatment == null) {
                preparedStatment = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            bindStatement();
            resultSet = preparedStatment.executeQuery();
            resultSet.setFetchSize(1);

            // populate results
            if (resultSet.next()) {
                return getObjectFromResultSet(resultSet, 0);
            }
            return null;
        } finally {
            DatabaseUtils.close(resultSet);
        }
    }

    public List<E> getObjects() throws SQLException {
        return getObjectsArrayList();
    }

    public ArrayList<E> getObjectsArrayList() throws SQLException {
        preparedStatment = null;
        ResultSet resultSet = null;
        try {
            ArrayList<E> results = new ArrayList<E>();
            preparedStatment = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            bindStatement();
            resultSet = preparedStatment.executeQuery();
            resultSet.setFetchDirection(ResultSet.FETCH_FORWARD);
            resultSet.setFetchSize(FETCH_SIZE);

            // populate results
            while (resultSet.next()) {
                results.add(getObjectFromResultSet(resultSet, 0));
            }
            return results;
        } finally {
            DatabaseUtils.close(resultSet);
            close();
        }
    }

    public E getObject() throws SQLException {
        preparedStatment = null;
        ResultSet resultSet = null;
        try {
            preparedStatment = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            bindStatement();
            resultSet = preparedStatment.executeQuery();
            resultSet.setFetchSize(1);

            // populate results
            if (resultSet.next()) {
                return getObjectFromResultSet(resultSet, 0);
            }
            return null;
        } finally {
            DatabaseUtils.close(resultSet);
            close();
        }
    }

    private void bindStatement() throws SQLException {
        for (int i = 0, size = binds.size(); i < size; i++) {
            Object value = binds.get(i);
            if (value != null) {
                preparedStatment.setObject(i + 1, value);
            } else {
                preparedStatment.setNull(i + 1, Types.NULL);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private E getObjectFromResultSet(ResultSet resultSet, int offset) throws SQLException {
        E result = (E) mapping.createNewObject();
        List<LoadFieldDefinition> fields = mapping.getFields();

        if (resultSetColumnIndexes == null || resultSetColumnTypes == null) {
            int size = fields.size();
            resultSetColumnIndexes = new int[size];
            resultSetColumnTypes = new int[size];
            for (int i = 0; i < size; i++) {
                LoadFieldDefinition field = (LoadFieldDefinition) fields.get(i);
                resultSetColumnIndexes[i] = -1;

                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int j = 1; j <= metaData.getColumnCount(); j++) {
                    if (field.getColumnName().equalsIgnoreCase(metaData.getColumnName(j))) {
                        resultSetColumnIndexes[i] = j;
                        resultSetColumnTypes[i] = metaData.getColumnType(j);
                        break;
                    }
                }

            }
        }

        for (int i = 0; i < resultSetColumnIndexes.length; i++) {
            int index = resultSetColumnIndexes[i];
            int type = resultSetColumnTypes[i];

            if (index > 0) {
                LoadFieldDefinition field = (LoadFieldDefinition) fields.get(i);
                boolean success = false;
                try {
                    Object value = DatabaseUtils.getObject(resultSet, index, type);
                    field.setValue(result, value);
                    success = true;
                } finally {
                    if (!success) {
                        Logger.getLogger(getClass().getName()).severe("Error loading column named: " + field.getColumnName() + " at index " + index + " of type " + type);
                    }
                }
            }
        }

        // If a dirty type object reset as not dirty
        if (!ignoreDirty && result instanceof Dirty) {
            ((Dirty) result).resetDirty();
        }
        return (E) result;

    }

}