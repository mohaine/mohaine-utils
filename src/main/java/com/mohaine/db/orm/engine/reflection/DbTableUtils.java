package com.mohaine.db.orm.engine.reflection;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

import com.mohaine.db.orm.DbField;
import com.mohaine.db.orm.DbTable;
import com.mohaine.db.orm.DbType;
import com.mohaine.db.orm.engine.LoadFieldDefinition;
import com.mohaine.db.orm.engine.ObjectTableMapping;
import com.mohaine.util.StringUtils;

public class DbTableUtils {
    private static ObjectDbNameMapper nameMapper = new ObjectDbNameMapper() {
        @Override
        public String mapClassToTable(Class<?> objectClass) {
            return objectClass.getSimpleName();
        }

        @Override
        public String mapFieldToColumn(Class<?> mappingClass, Field field) {
            return field.getName();
        }

        @Override
        public DbType mapFieldToType(Class<?> mappingClass, Field field) {
            if (String.class.isAssignableFrom(field.getType())) {
                return DbType.String;
            } else if (Integer.class.isAssignableFrom(field.getType()) || Integer.TYPE.isAssignableFrom(field.getType())) {
                return DbType.Integer;
            } else if (Long.class.isAssignableFrom(field.getType()) || Long.TYPE.isAssignableFrom(field.getType())) {
                return DbType.Long;
            } else if (Double.class.isAssignableFrom(field.getType()) || Double.TYPE.isAssignableFrom(field.getType())) {
                return DbType.Double;
            } else if (Date.class.isAssignableFrom(field.getType())) {
                return DbType.Date;
            } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
                return DbType.BigDecimal;
            }
            return DbType.String;
        }

        @Override
        public boolean isFieldKey(Class<?> mappingClass, Field field) {
            return field.getName().equals("id");
        }
    };

    public interface ObjectDbNameMapper {

        String mapClassToTable(Class<?> objectClass);

        String mapFieldToColumn(Class<?> mappingClass, Field field);

        DbType mapFieldToType(Class<?> mappingClass, Field field);

        boolean isFieldKey(Class<?> mappingClass, Field field);
    }

    public static ObjectTableMapping generateMapping(Class<?> objectClass) {
        ReflectionKeyedObjectTableMapping reflectionMapping = new ReflectionKeyedObjectTableMapping(objectClass);
        DbTable dbTable = (DbTable) objectClass.getAnnotation(DbTable.class);

        if (dbTable != null && StringUtils.hasLength((dbTable.tableName()))) {
            reflectionMapping.setTableName(dbTable.tableName());
        } else {
            reflectionMapping.setTableName(nameMapper.mapClassToTable(objectClass));
        }

        addFields(objectClass, reflectionMapping, false, false, false);
        return reflectionMapping;
    }

    private static void addFields(Class<?> mappingClass, ReflectionKeyedObjectTableMapping reflectionMapping, boolean forceLoadOnly, boolean inheritParentKeys, boolean inheritParentKeysAsField) {

        Class<?> superclass = mappingClass.getSuperclass();

        if (superclass != null) {
            boolean saveParentFields = false;
            boolean inheritParentKeys1 = false;
            boolean inheritParentKeysAsField1 = false;

            DbTable dbTable = (DbTable) mappingClass.getAnnotation(DbTable.class);
            if (dbTable != null) {
                saveParentFields = dbTable.saveParentFields();
                inheritParentKeys1 = dbTable.inheritParentKeys();
                inheritParentKeysAsField1 = dbTable.inheritParentKeysAsField();
            }
            addFields(superclass, reflectionMapping, !saveParentFields, inheritParentKeys1, inheritParentKeysAsField1);
        }

        Field[] declaredFields = mappingClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];

            DbField dbField = field.getAnnotation(DbField.class);

            boolean callSetter;
            boolean isFieldKey;
            String columnName;
            boolean parentKey;
            DbType type = null;
            boolean postSelectKey;
            String sequenceName;
            String modifyBind;
            boolean dbFieldLoadOnly;

            if (dbField != null) {
                callSetter = dbField.callSetter();
                isFieldKey = dbField.key();
                columnName = dbField.columnName();
                parentKey = dbField.parentKey();
                type = dbField.type();
                postSelectKey = dbField.postSelectKey();
                sequenceName = dbField.sequenceName();
                modifyBind = dbField.modifyBind();
                dbFieldLoadOnly = dbField.loadOnly();
            } else if (mappingClass.isAnnotationPresent(DbTable.class)) {
                // If has DbTable, only include fields with DbField
                continue;
            } else {
                callSetter = true;
                isFieldKey = nameMapper.isFieldKey(mappingClass, field);
                columnName = nameMapper.mapFieldToColumn(mappingClass, field);
                parentKey = false;
                postSelectKey = false;
                sequenceName = "";
                modifyBind = "";
                dbFieldLoadOnly = false;
            }
            if (type == null) {
                type = nameMapper.mapFieldToType(mappingClass, field);
            }


            LoadFieldDefinition ldf;
            boolean loadOnly = forceLoadOnly || dbFieldLoadOnly;

            boolean key = isFieldKey;

            if (inheritParentKeys) {
                loadOnly = !key;
            }

            if (inheritParentKeysAsField && key) {
                loadOnly = false;
                key = false;
            }

            if (loadOnly) {
                ldf = new ReflectionLoadFieldDefinition(columnName, field, type);
            } else {
                ReflectionFieldDefinition rfd = new ReflectionFieldDefinition(columnName, field, type);
                ldf = rfd;
                if (parentKey) {
                    reflectionMapping.setParentKey(rfd);
                }
                rfd.setPostSelectKey(postSelectKey);
                rfd.setSequenceName(sequenceName);
                rfd.setModifyBind(modifyBind);
            }

            if (callSetter) {
                String name = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                Method[] methods = mappingClass.getMethods();
                boolean found = false;
                for (int j = 0; j < methods.length; j++) {
                    Method method = methods[j];
                    if (method.getName().equals(name)) {
                        if (ldf instanceof ReflectionFieldDefinition) {
                            ((ReflectionFieldDefinition) ldf).setSetter(method);
                        } else if (ldf instanceof ReflectionLoadFieldDefinition) {
                            ((ReflectionLoadFieldDefinition) ldf).setSetter(method);
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new RuntimeException("Unable to find setter " + name + " on " + mappingClass.getName());
                }
            }

            if (loadOnly) {
                key = false;
            }
            if (key) {
                reflectionMapping.addKey((ReflectionFieldDefinition) ldf);
            } else {
                reflectionMapping.addField(ldf);
            }
        }
    }


}
