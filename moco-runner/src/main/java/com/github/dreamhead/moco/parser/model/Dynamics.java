package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.google.common.base.Predicate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

public class Dynamics {
    protected Predicate<Field> isClassField() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return "class".equals(field.getName());
            }
        };
    }

    protected Predicate<Field> isFinalField() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return Modifier.isFinal(field.getModifiers());
            }
        };
    }

    protected <T> Predicate<Field> fieldExist(final T target) {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                try {
                    return field.get(target) != null;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    protected Iterable<Field> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return Arrays.asList(fields);
    }

    protected <T> Predicate<Field> isValidField(T target) {
        return and(not(or(isClassField(), isFinalField())), fieldExist(target));
    }

    protected <T> T invokeTarget(String name, Object value, Class<T> clazz) {
        return invokeTarget(name, value, clazz, value.getClass());
    }

    private <T> T invokeTarget(String name, Object value, Class<T> clazz, Class<?> argClass) {
        try {
            Method method = Moco.class.getMethod(name, argClass);
            Object result = method.invoke(null, value);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T invokeTarget(String name, int value, Class<T> clazz) {
        return invokeTarget(name, value, clazz, Integer.TYPE);
    }

    protected <T> T invokeTarget(String name, long value, Class<T> clazz) {
        return invokeTarget(name, value, clazz, Long.TYPE);
    }
}
