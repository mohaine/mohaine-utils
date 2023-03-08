package com.mohaine.db.orm;

import com.mohaine.db.DatabaseUtils;
import com.mohaine.db.orm.engine.LoadFieldDefinition;
import com.mohaine.db.orm.engine.MappingCache;
import com.mohaine.db.orm.engine.ObjectTableMapping;
import com.mohaine.util.CreateNewObject;
import com.mohaine.util.ReflectionObjectBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OrmRecordLoader<E> implements AutoCloseable {
    private ReflectionObjectBuilder reflectionObjectBuilder;
    private static final int FETCH_SIZE = 1000;
    private final Connection connection;
    private final String sql;
    private PreparedStatement preparedStatment = null;
    private int[] resultSetColumnTypes = null;
    private String[] resultSetColumnNames = null;

    public OrmRecordLoader(Connection connection, String sql, Class<E> mappingClass) {
        super();
        this.sql = sql;
        this.connection = connection;
        this.reflectionObjectBuilder = new ReflectionObjectBuilder<E>(mappingClass);
    }


    public void close() {
        DatabaseUtils.close(preparedStatment);
        resultSetColumnTypes = null;
        resultSetColumnNames = null;
        preparedStatment = null;
    }


    public List<E> getObjects(Object... binds) throws SQLException {
        setupStatment(binds);
        try (ResultSet resultSet = preparedStatment.executeQuery()) {
            resultSet.setFetchDirection(ResultSet.FETCH_FORWARD);
            resultSet.setFetchSize(FETCH_SIZE);

            // populate results
            ArrayList<E> results = new ArrayList<E>();
            while (resultSet.next()) {
                results.add(getObjectFromResultSet(resultSet, 0));
            }
            return results;
        }
    }

    private void setupStatment(Object... binds) throws SQLException {
        if (preparedStatment == null) {
            preparedStatment = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
        bindStatement(binds);
    }

    public E getObject(Object... binds) throws SQLException {
        setupStatment(binds);
        try (ResultSet resultSet = preparedStatment.executeQuery()) {
            resultSet.setFetchSize(1);

            // populate results
            if (resultSet.next()) {
                return getObjectFromResultSet(resultSet, 0);
            }
            return null;
        }
    }

    private void bindStatement(Object... binds) throws SQLException {
        for (int i = 0, size = binds.length; i < size; i++) {
            Object value = binds[i];
            if (value != null) {
                preparedStatment.setObject(i + 1, value);
            } else {
                preparedStatment.setNull(i + 1, Types.NULL);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private E getObjectFromResultSet(ResultSet resultSet, int offset) throws SQLException {

        if (resultSetColumnTypes == null || resultSetColumnNames == null) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int size = metaData.getColumnCount();

            resultSetColumnTypes = new int[size];
            resultSetColumnNames = new String[size];

            for (int i = 0; i < size; i++) {
                resultSetColumnTypes[i] = metaData.getColumnType(i + 1);
                resultSetColumnNames[i] = metaData.getColumnLabel(i + 1);
            }
        }

        Map<String, Object> columnValues = new HashMap<>();

        for (int i = 0; i < resultSetColumnTypes.length; i++) {
            String name = resultSetColumnNames[i];
            int type = resultSetColumnTypes[i];

            boolean success = false;
            try {
                Object value = DatabaseUtils.getObject(resultSet, i + 1, type);
                columnValues.put(name, value);
                success = true;
            } finally {
                if (!success) {
                    Logger.getLogger(getClass().getName()).severe("Error loading column named: " + name + " at index " + i + " of type " + type);
                }
            }
        }
        CreateNewObject<E> createdObject = this.reflectionObjectBuilder.createNewObject(columnValues);
        return createdObject.obj();
    }

}
