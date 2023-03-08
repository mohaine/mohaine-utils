package com.mohaine.util;

import com.mohaine.json.JsonObjectHandlerBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.*;

public class ReflectionObjectBuilder<T> {

    private final Constructor<T> constructor;
    private Class<T> classToBuild;
    private final Map<String, Integer> nameToIndexMap = new HashMap<>();
    private final Type[] constructorGenericTypes;


    public ReflectionObjectBuilder(Class<T> classToBuild) {
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
    public CreateNewObject<T> createNewObject(Map<String, Object> namesToValues) {

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

        // TODO handle unknownNames

        try {
            return new CreateNewObject<T>((T) constructor.newInstance(args), unhandledNames) ;
        } catch (InstantiationException | SecurityException | IllegalAccessException |
                 IllegalArgumentException | InvocationTargetException e) {


            System.out.println("types: " + Arrays.asList(constructorGenericTypes).stream().map(i -> i.getTypeName()).toList());
            System.out.println("args : " + Arrays.asList(args).stream().map(i -> {
                if (i == null) {
                    return null;
                }
                return i.getClass().getName();
            }).toList());
            throw new RuntimeException(e);
        }
    }
}
