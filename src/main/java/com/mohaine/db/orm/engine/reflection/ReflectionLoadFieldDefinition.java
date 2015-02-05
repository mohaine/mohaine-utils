package com.mohaine.db.orm.engine.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mohaine.db.orm.DbType;
import com.mohaine.db.orm.engine.AbstractLoadFieldDefinition;
import com.mohaine.util.StringUtils;

public class ReflectionLoadFieldDefinition extends AbstractLoadFieldDefinition {
	private Method setter;

	private final Field field;
	private final DbType dbType;

	public ReflectionLoadFieldDefinition(String columnName, Field field,
			DbType dbType) {
		super(columnName);
		this.field = field;
		field.setAccessible(true);
		this.dbType = dbType;
	}

	@Override
	public void setValue(Object object, Object value) {
		try {
			Object valueToSet = getValueToSet(field, dbType, value);
			if (this.setter != null) {
				this.setter.invoke(object, valueToSet);
			} else {
				field.set(object, valueToSet);
			}
		} catch (ClassCastException e) {
			throw new RuntimeException(
					"IllegalArgumentException setting field: "
							+ getColumnName() + " on class "
							+ field.getDeclaringClass().getName(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
					"IllegalArgumentException setting field: "
							+ getColumnName() + " on class "
							+ field.getDeclaringClass().getName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IllegalAccessException setting field: "
					+ getColumnName() + " on class "
					+ field.getDeclaringClass().getName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(
					"InvocationTargetException setting field: "
							+ getColumnName() + " on class "
							+ field.getDeclaringClass().getName(), e);
		}

	}

	static Object getValueToSet(Field field, DbType type, Object value) {
		if (DbType.Integer.equals(type)) {
			return value != null ? new Integer(((Number) value).intValue())
					: null;
		} else if (DbType.Double.equals(type)) {
			return value != null ? new Double(((Number) value).doubleValue())
					: null;
		} else if (DbType.Long.equals(type)) {
			return value != null ? new Long(((Number) value).longValue())
					: null;
		} else if (DbType.String.equals(type)) {
			return StringUtils.toString(value);
		} else if (DbType.UUID.equals(type)) {
			String uuid = StringUtils.toString(value);
			uuid = uuid.replaceAll("[-{}]", "");
			return uuid;
		}
		return value;
	}

	public Method getSetter() {
		return setter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

}
