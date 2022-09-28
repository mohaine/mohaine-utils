package com.mohaine.json;


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
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }

        @Override
        public Class<?> getExpectedType() {
            return field.getType();
        }

//        public Class<?> getGenericType() {
//            return (Class<T>)
//                    ((ParameterizedType) getExpectedType().getGenericSuperclass())
//                            .getActualTypeArguments()[0];
//        }


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

                field.set(object, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set value " + value + " on " + field.getDeclaringClass().getName() + "." + field.getName(), e);
            }

        }
    }


    private static final class ReflectionJsonObjectHandler<T> extends JsonObjectHandlerBase<T> {
        private final Constructor<T> constructor;
        private Class<T> classToBuild;
        ArrayList<JsonObjectPropertyHandler<T, ?>> phs = new ArrayList<JsonObjectPropertyHandler<T, ?>>();
        private final Map<String, Integer> nameToIndexMap = new HashMap<>();
        private final Type[] constructorGenericTypes;


        public ReflectionJsonObjectHandler(Class<T> classToBuild) {
            this.classToBuild = classToBuild;
            var rawTypes = new Class<?>[0];
            RecordComponent[] recordComponents = classToBuild.getRecordComponents();
            if (recordComponents == null) {
                constructorGenericTypes = new Type[0];
            } else {
                rawTypes = new Class<?>[recordComponents.length];
                constructorGenericTypes = new Type[recordComponents.length];
                var index = 0;
                for (RecordComponent recordComponent : recordComponents) {
                    constructorGenericTypes[index] = recordComponent.getGenericType();
                    rawTypes[index] = recordComponent.getType();
                    nameToIndexMap.put(recordComponent.getName(), index);
                    index += 1;
                }
            }
            try {
                this.constructor = classToBuild.getDeclaredConstructor(rawTypes);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            this.constructor.setAccessible(true);
        }

        public Class<T> getType() {
            return classToBuild;
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
            Object[] args = new Object[constructorGenericTypes.length];

            Set<String> unhandledNames = new HashSet<>();

            namesToValues.entrySet().forEach(e -> {
                var index = nameToIndexMap.get(e.getKey());
                if (index != null) {
                    args[index] = e.getValue();
                } else {
                    unhandledNames.add(e.getKey());
                }
            });

            try {
                return new CreateNewObject((T) constructor.newInstance(args), unhandledNames);
            } catch (InstantiationException | SecurityException | IllegalAccessException |
                     IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public void addPropertyHandler(JsonObjectPropertyHandler<T, ?> propertyHandler) {
            phs.add(propertyHandler);
        }

    }

    public static <T> JsonObjectHandler<T> build(final Class<T> classToBuild) throws Exception {
        ReflectionJsonObjectHandler<T> joh = new ReflectionJsonObjectHandler<T>(classToBuild);
        addFields(joh, classToBuild);
        return joh;
    }


    private static <T> void addFields(ReflectionJsonObjectHandler<T> joh, Class<?> objClass) throws Exception {
        Class<?> superClass = objClass.getSuperclass();

        if (superClass != null) {
            addFields(joh, superClass);
        }
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
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
