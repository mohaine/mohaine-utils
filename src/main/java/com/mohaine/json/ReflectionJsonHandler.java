package com.mohaine.json;


import com.mohaine.util.CreateNewObject;
import com.mohaine.util.ReflectionObjectBuilder;

import java.lang.reflect.*;
import java.util.*;


@SuppressWarnings("unchecked")
public class ReflectionJsonHandler {

    private static final class ReflectionJsonPropertyHandler<T, F> extends JsonObjectPropertyHandler<T, F> {
        private final Field field;

        public ReflectionJsonPropertyHandler(Field field) {
            this.field = field;
            field.setAccessible(true);
        }

        @Override
        public String getName() {
            return field.getName();
        }

        @Override
        public Class<?> getGenericType() {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
            return null;
        }

        @Override
        public Class<?> getExpectedType() {
            return field.getType();
        }

        @Override
        public boolean isJson() {
            return field.isAnnotationPresent(JsonString.class);
        }

        @Override
        public F getValue(T object) {
            try {
                Object value = field.get(object);
                if (value instanceof Enum) {
                    return (F) value.toString();
                }
                return (F) value;
            } catch (Exception e) {
                throw new RuntimeException("Failed to get value " + object + " on " + field.getDeclaringClass().getName() + "." + field.getName(), e);
            }
        }

        @Override
        public void setValue(T object, F value) {
            try {
                field.set(object, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set value " + value + " on " + field.getDeclaringClass().getName() + "." + field.getName(), e);
            }

        }

        @Override
        public F mapValue(F value) {
            if (value != null) {
                if (field.getType().isEnum()) {
                    Object[] enums = field.getType().getEnumConstants();
                    for (Object enumValue : enums) {
                        if (enumValue.toString().equals(value)) {

                            value = (F) enumValue;
                            break;
                        }
                    }
                }
                if (value instanceof JsonUnknownObject) {
                    if (Map.class.isAssignableFrom(field.getType())) {
                        JsonUnknownObject juo = (JsonUnknownObject) value;
                        value = (F) juo.getProperties();
                    }
                }
                if (!field.getType().isAssignableFrom(value.getClass())) {

                    if (Integer.class.isAssignableFrom(field.getType())) {
                        if (value instanceof Long) {
                            value = (F) Integer.valueOf(((Long) value).intValue());
                        }
//                    } else if (Long.TYPE.equals(field.getType())) {
//                        value = (F) Long.valueOf(0);
//                    } else if (Double.TYPE.equals(field.getType())) {
//                        value = (F) Double.valueOf(0);
//                    } else if (Float.TYPE.equals(field.getType())) {
//                        value = (F) Float.valueOf(0);
//                    } else if (Byte.TYPE.equals(field.getType())) {
//                        value = (F) Byte.valueOf((byte) 0);
                    } else {
                        throw new RuntimeException(" Need to map: " + value.getClass().getName() + " -> " + field.getType().getName());
                    }
                }

            } else {
                if (Boolean.TYPE.equals(field.getType())) {
                    value = (F) Boolean.FALSE;
                } else if (Integer.TYPE.equals(field.getType())) {
                    value = (F) Integer.valueOf(0);
                } else if (Long.TYPE.equals(field.getType())) {
                    value = (F) Long.valueOf(0);
                } else if (Double.TYPE.equals(field.getType())) {
                    value = (F) Double.valueOf(0);
                } else if (Float.TYPE.equals(field.getType())) {
                    value = (F) Float.valueOf(0);
                } else if (Byte.TYPE.equals(field.getType())) {
                    value = (F) Byte.valueOf((byte) 0);
                }
            }
            return value;
        }
    }


    private static final class ReflectionJsonObjectHandler<T> extends JsonObjectHandlerBase<T> {
        private final ReflectionObjectBuilder reflectionObjectBuilder;
        private final Class<T> classToBuild;
        ArrayList<JsonObjectPropertyHandler<T, ?>> phs = new ArrayList<JsonObjectPropertyHandler<T, ?>>();


        public ReflectionJsonObjectHandler(Class<T> classToBuild) {
            this.classToBuild=classToBuild;
            this.reflectionObjectBuilder  = new ReflectionObjectBuilder<T>(classToBuild);
        }

        public Class<T> getType() {
            return this.classToBuild;
        }

        @Override
        public boolean handlesType(Class<?> value) {
            return classToBuild.isAssignableFrom(value);
        }

        @Override
        public List<JsonObjectPropertyHandler<T, ?>> getPropertyHandlers() {
            return phs;
        }


        @Override
        protected CreateNewObject<T> createNewObject(HashMap<String, Object> namesToValues) {
            return this.reflectionObjectBuilder.createNewObject(namesToValues);
        }

        public void addPropertyHandler(JsonObjectPropertyHandler<T, ?> propertyHandler) {
            phs.add(propertyHandler);
        }

    }

    public static <T> ReflectionJsonObjectHandler<T> build(final Class<T> classToBuild) throws Exception {
        ReflectionJsonObjectHandler<T> joh = new ReflectionJsonObjectHandler<T>(classToBuild);
        addFields(joh, classToBuild);
        return joh;
    }

    public static void buildAll(JsonConverterConfig config, final Class<?> classToBuild) throws Exception {
        if (classToBuild != null && !config.canHandle(classToBuild)) {
            ReflectionJsonObjectHandler<?> handler = build(classToBuild);
            config.addHandler(handler);

            for (var fieldHandler : handler.phs) {
                buildAll(config, fieldHandler.getExpectedType());
                buildAll(config, fieldHandler.getGenericType());
            }
        }
    }


    private static <T> void addFields(ReflectionJsonObjectHandler<T> joh, Class<?> objClass) throws Exception {
        Class<?> superClass = objClass.getSuperclass();

        if (superClass != null) {
            addFields(joh, superClass);
        }
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            joh.addPropertyHandler(new ReflectionJsonPropertyHandler<T, Object>(field));
        }
    }
}
