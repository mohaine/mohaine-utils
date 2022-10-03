/*
 * Created on Jun 7, 2007
 *
 */
package com.mohaine.db.orm.engine;

import com.mohaine.db.orm.engine.reflection.DbTableUtils;

import java.util.HashMap;
import java.util.Map;

public class MappingCache {

    private static Map<Class<?>, ObjectTableMapping> mappings = new HashMap<Class<?>, ObjectTableMapping>();

    public static ObjectTableMapping getMapping(Class<?> mappingClass) {
        ObjectTableMapping mapping;

        mapping = mappings.get(mappingClass);

        mapping = null;

        if (mapping == null) {
            try {
                if (ObjectTableMapping.class.isAssignableFrom(mappingClass)) {
                    mapping = (ObjectTableMapping) mappingClass.newInstance();
                } else {
                    mapping = DbTableUtils.generateMapping(mappingClass);
                    if (mapping == null) {
                        throw new RuntimeException(
                                "Could not get mapping for class "
                                        + mappingClass.getName());
                    }
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
            mappings.put(mappingClass, mapping);
        }
        return mapping;
    }

}
