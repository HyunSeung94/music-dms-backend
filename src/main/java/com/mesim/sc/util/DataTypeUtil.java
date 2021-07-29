package com.mesim.sc.util;

import com.mesim.sc.constants.CodeConstants;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public class DataTypeUtil {

    public static String checkType(Object value) {
        String type = value.getClass().getName();

        switch (type) {
            case "java.lang.Byte":
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.lang.Long":
                return CodeConstants.FIELD_TYPE_INTEGER;
            case "java.lang.Double":
            case "java.lang.Float":
                return CodeConstants.FIELD_TYPE_REAL;
            case "java.lang.Boolean":
                return CodeConstants.FIELD_TYPE_BOOLEAN;
            case "java.lang.String":
            default:
                return CodeConstants.FIELD_TYPE_STRING;
        }
    }

    public static Object parseType(String type, Object value) {
        String valueStr = (String) value;

        try {
            switch (type) {
                case CodeConstants.FIELD_TYPE_INTEGER:
                    return Integer.valueOf(valueStr);
                case CodeConstants.FIELD_TYPE_REAL:
                    return Double.valueOf(valueStr);
                case CodeConstants.FIELD_TYPE_BOOLEAN:
                    return Boolean.valueOf(valueStr);
                case CodeConstants.FIELD_TYPE_STRING:
                default:
                    return valueStr;
            }
        } catch(Exception e) {
            return valueStr;
        }
    }

    public static Type[] getGenericType(Class<?> target) {
        if (target == null)
            return new Type[0];
        Type[] types = target.getGenericInterfaces();
        if (types.length > 0) {
            return types;
        }
        Type type = target.getGenericSuperclass();
        if (type != null) {
            if (type instanceof ParameterizedType) {
                return new Type[] { type };
            }
        }
        return new Type[0];
    }

    @SuppressWarnings("rawtypes")
    public static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
