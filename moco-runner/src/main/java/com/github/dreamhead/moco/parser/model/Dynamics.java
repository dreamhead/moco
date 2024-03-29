package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.concat;

public class Dynamics {
    private static final Map<String, String> EXTRACTORS = ImmutableMap.<String, String>builder()
            .put("headers", "header")
            .put("queries", "query")
            .put("xpaths", "xpath")
            .put("jsonPaths", "jsonPath")
            .put("cookies", "cookie")
            .put("forms", "form")
            .build();

    protected final Predicate<Field> isClassField() {
        return field -> "class".equals(field.getName());
    }

    protected final Predicate<Field> isFinalField() {
        return field -> Modifier.isFinal(field.getModifiers());
    }

    protected final <T> Predicate<Field> fieldExist(final T target) {
        return field -> {
            try {
                return field.get(target) != null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    protected final Iterable<Field> getFields(final Class<?> clazz) {
        ImmutableList<Field> fieldsForCurrent = getFieldsForCurrent(clazz);
        if (clazz.getSuperclass() == null) {
            return fieldsForCurrent;
        }

        return concat(getFields(clazz.getSuperclass()), fieldsForCurrent);
    }

    private ImmutableList<Field> getFieldsForCurrent(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    field.setAccessible(true);
                    return field;
                })
                .collect(toImmutableList());
    }

    protected final <T> Predicate<Field> isValidField(final T target) {
        return isClassField().or(isFinalField()).negate().and(fieldExist(target));
    }

    public static <T> T invokeTarget(final String name, final Object value, final Class<T> clazz) {
        return invokeTarget(name, value, clazz, value.getClass());
    }

    public static <T> T invokeTarget(final String name, final Object value,
                                     final Class<T> clazz, final Class<?> argClass) {
        try {
            Method method = Moco.class.getMethod(name, argClass);
            Object result = method.invoke(null, value);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invokeTarget(final String name, final Object arg1, final Object arg2,
                                     final Class<T> clazz, final Class<?> arg1Class, final Class<?> arg2Class) {
        try {
            Method method = Moco.class.getMethod(name, arg1Class, arg2Class);
            Object result = method.invoke(null, arg1, arg2);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Method getExtractorMethod(final String name) {
        try {
            if (EXTRACTORS.containsKey(name)) {
                return Moco.class.getMethod(EXTRACTORS.get(name), String.class);
            }

            throw new RuntimeException("No [" + name + "] extractor found");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected static RequestExtractor createRequestExtractor(final Method method, final String key) {
        try {
            return RequestExtractor.class.cast(method.invoke(null, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
