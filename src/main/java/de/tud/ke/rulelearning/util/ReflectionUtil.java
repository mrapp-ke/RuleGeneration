package de.tud.ke.rulelearning.util;

import java.lang.reflect.Field;

public final class ReflectionUtil {

    private ReflectionUtil() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T getDeclaredField(final Object object, final String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInheritedField(final Object object, final String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field = null;

            while (clazz != Object.class && field == null) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    // Continue in superclass
                }

                clazz = clazz.getSuperclass();
            }

            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(object);
            }

            throw new NoSuchFieldException(fieldName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
