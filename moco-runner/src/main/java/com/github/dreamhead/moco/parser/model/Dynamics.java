package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;

public class Dynamics {
    private static final Map<String, String> extractorMethods = ImmutableMap.<String, String>builder()
            .put("headers", "header")
            .put("queries", "query")
            .put("xpaths", "xpath")
            .put("jsonPaths", "jsonPath")
            .put("cookies", "cookie")
            .put("forms", "form").build();

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
        ImmutableList<Field> fieldsForCurrent = getFieldsForCurrent(clazz);
        if (clazz.getSuperclass() == null) {
            return fieldsForCurrent;
        }

        return concat(getFields(clazz.getSuperclass()), fieldsForCurrent);
    }

    private ImmutableList<Field> getFieldsForCurrent(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }

        return copyOf(fields);
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

    protected <T> T invokeTarget(String name, Object arg1, Object arg2, Class<T> clazz, Class<?> arg1Class, Class<?> arg2Class) {
        try {
            Method method = Moco.class.getMethod(name, arg1Class, arg2Class);
            Object result = method.invoke(null, arg1, arg2);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Method getExtractorMethod(String name) {
        try {
            return Moco.class.getMethod(extractorMethods.get(name), String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected static RequestExtractor createRequestExtractor(Method method, String key) {
        try {
            return RequestExtractor.class.cast(method.invoke(null, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
