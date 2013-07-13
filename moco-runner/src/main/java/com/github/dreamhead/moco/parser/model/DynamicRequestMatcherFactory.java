package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.parser.RequestMatcherFactory;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.google.common.base.Predicates.*;
import static com.google.common.collect.FluentIterable.from;

public class DynamicRequestMatcherFactory implements RequestMatcherFactory {
    private final Map<String, String> methods = ImmutableMap.<String,String>builder()
    		.put("headers", "header")
    		.put("queries", "query")
    		.put("xpaths", "xpath")
    		.put("jsonPaths", "jsonPath")
    		.put("cookies", "cookie")
    		.put("forms", "form").build();

    @Override
    public RequestMatcher createRequestMatcher(RequestSetting request) {
        return wrapRequestMatcher(request, createRequestMatchers(request));
    }

    private Collection<RequestMatcher> createRequestMatchers(final RequestSetting request) {
        return from(getFields()).filter(and(not(or(isClassField(), isFinalField())), fieldExist(request))).transform(fieldToRequestMatcher(request)).toList();
    }

    private Iterable<Field> getFields() {
        Field[] fields = RequestSetting.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return Arrays.asList(fields);
    }

    private Predicate<Field> fieldExist(final RequestSetting request) {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                try {
                    return field.get(request) != null;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Function<Field, RequestMatcher> fieldToRequestMatcher(final RequestSetting request) {
        return new Function<Field, RequestMatcher>() {
            @Override
            public RequestMatcher apply(Field field) {
                try {
                    Object value = field.get(request);
                    return createRequestMatcherFromValue(field.getName(), value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Predicate<Field> isClassField() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return "class".equals(field.getName());
            }
        };
    }

    private Predicate<Field> isFinalField() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return Modifier.isFinal(field.getModifiers());
            }
        };
    }

    private RequestMatcher createRequestMatcherFromValue(String name, Object value) {
        if (Map.class.isInstance(value)) {
            return createCompositeMatcher(name, castToMap(value));
        }

        if (TextContainer.class.isInstance(value)) {
            return createSingleTextMatcher(name, TextContainer.class.cast(value));
        }

        throw new IllegalArgumentException("unknown configuration :" + value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object value) {
        return Map.class.cast(value);
    }

    private RequestMatcher createSingleMatcher(String name, String value) {
        return by(createResource(name, value));
    }

    private Resource createResource(String name, String value) {
        try {
            Method method = Moco.class.getMethod(name, String.class);
            Object result = method.invoke(null, value);
            return Resource.class.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestMatcher createSingleTextMatcher(String name, TextContainer container) {
        if (container.isRawText()) {
            return createSingleMatcher(name, container.getText());
        }

        return createRequestMatcher(container.getOperation(), createResource(name, container.getText()));
    }

    private RequestMatcher createRequestMatcher(String operation, Resource resource) {
        try {
            Method operationMethod = Moco.class.getMethod(operation, Resource.class);
            return RequestMatcher.class.cast(operationMethod.invoke(null, resource));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestMatcher createCompositeMatcher(String name, Map<String, Object> collection) {
        List<RequestMatcher> matchers = from(collection.entrySet()).transform(toTargetMatcher(getMethodForCompositeMatcher(name))).toList();
        return wrapRequestMatcher(null, matchers);
    }

    private Function<Map.Entry<String, Object>, RequestMatcher> toTargetMatcher(final Method extractorMethod) {
        return new Function<Map.Entry<String, Object>, RequestMatcher>() {
            @Override
            @SuppressWarnings("unchecked")
            public RequestMatcher apply(Map.Entry<String, Object> pair) {
                RequestExtractor extractor = createRequestExtractor(extractorMethod, pair.getKey());
                return createRequestMatcher(extractor, pair.getValue());
            }
        };
    }

    @SuppressWarnings("unchecked")
    private RequestExtractor createRequestExtractor(Method method, String key) {
        try {
            return RequestExtractor.class.cast(method.invoke(null, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> RequestMatcher createRequestMatcher(RequestExtractor<T> extractor, Object value) {
        if (TextContainer.class.isInstance(value)) {
            return getRequestMatcher(extractor, TextContainer.class.cast(value));
        }

        throw new IllegalArgumentException("unknown value type: " + value);
    }

    private <T> RequestMatcher getRequestMatcher(RequestExtractor<T> extractor, TextContainer container) {
        if (container.isRawText()) {
            return eq(extractor, container.getText());
        }

        try {
            Method operationMethod = Moco.class.getMethod(container.getOperation(), RequestExtractor.class, String.class);
            Object result = operationMethod.invoke(null, extractor, container.getText());
            return RequestMatcher.class.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Method getMethodForCompositeMatcher(String name) {
        try {
            return Moco.class.getMethod(methods.get(name), String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static RequestMatcher wrapRequestMatcher(RequestSetting request, Collection<RequestMatcher> matchers) {
        switch (matchers.size()) {
            case 0:
                throw new IllegalArgumentException("illegal request setting:" + request);
            case 1:
                return matchers.iterator().next();
            default:
                return new AndRequestMatcher(matchers);
        }
    }
}
