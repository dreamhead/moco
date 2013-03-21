package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableMap.of;

public class DynamicRequestMatcherParser implements RequestMatcherParser {
    private Map<String, String> methods = of(
            "headers", "header",
            "queries", "query",
            "xpaths", "xpath",
            "cookies", "cookie"
    );

    @Override
    public RequestMatcher createRequestMatcher(RequestSetting request) {
        return wrapRequestMatcher(request, createRequestMatchers(request));
    }

    private Collection<RequestMatcher> createRequestMatchers(final RequestSetting request) {
        return from(getPropertyDescriptors()).filter(and(not(classField()), existField(request))).transform(toRequestMatcher(request)).toList();
    }

    private List<PropertyDescriptor> getPropertyDescriptors() {
        return Arrays.asList(doGetPropertyDescriptors());
    }

    private Function<PropertyDescriptor, RequestMatcher> toRequestMatcher(final RequestSetting request) {
        return new Function<PropertyDescriptor, RequestMatcher>() {
            @Override
            public RequestMatcher apply(PropertyDescriptor descriptor) {
                try {
                    Object value = descriptor.getReadMethod().invoke(request);
                    return createRequestMatcherFromValue(descriptor.getName(), value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Predicate<PropertyDescriptor> existField(final RequestSetting request) {
        return new Predicate<PropertyDescriptor>() {
            @Override
            public boolean apply(PropertyDescriptor descriptor) {
                try {
                    return descriptor.getReadMethod().invoke(request) != null;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Predicate<PropertyDescriptor> classField() {
        return new Predicate<PropertyDescriptor>() {
            @Override
            public boolean apply(PropertyDescriptor descriptor) {
                return "class".equals(descriptor.getName());
            }
        };
    }

    private PropertyDescriptor[] doGetPropertyDescriptors() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(RequestSetting.class);
            return beanInfo.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
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
            public RequestMatcher apply(Map.Entry<String, Object> pair) {
                RequestExtractor extractor = createRequestExtractor(extractorMethod, pair.getKey());
                return createRequestMatcher(extractor, pair.getValue());
            }
        };
    }

    private RequestExtractor createRequestExtractor(Method method, String key) {
        try {
            return RequestExtractor.class.cast(method.invoke(null, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestMatcher createRequestMatcher(RequestExtractor extractor, Object value) {
        if (TextContainer.class.isInstance(value)) {
            return getRequestMatcher(extractor, TextContainer.class.cast(value));
        }

        throw new IllegalArgumentException("unknown value type: " + value);
    }

    private RequestMatcher getRequestMatcher(RequestExtractor extractor, TextContainer container) {
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
