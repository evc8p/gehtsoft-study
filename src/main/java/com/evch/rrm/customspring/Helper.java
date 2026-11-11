package com.evch.rrm.customspring;

public class Helper {
    static Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == byte.class || targetType == Byte.class)
            return Byte.parseByte(value);
        if (targetType == short.class || targetType == Short.class)
            return Short.parseShort(value);
        if (targetType == int.class || targetType == Integer.class)
            return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class)
            return Long.parseLong(value);
        if (targetType == float.class || targetType == Float.class)
            return Float.parseFloat(value);
        if (targetType == double.class || targetType == Double.class)
            return Double.parseDouble(value);
        if (targetType == boolean.class || targetType == Boolean.class)
            return Boolean.parseBoolean(value);
        if (targetType.isEnum())
            return Enum.valueOf((Class<Enum>) targetType, value);
        throw new RuntimeException("Unsupported @CustomValue type: " + targetType);
    }
}
