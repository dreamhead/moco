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
import com.google.common.collect.ImmutableMap;

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

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

public class DynamicRequestMatcherParser implements RequestMatcherParser {
    private Map<String, String> methods = ImmutableMap.of(
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
        return transform(filter(getPropertyDescriptors(), and(not(classField()), existField(request))), toRequestMatcher(request));
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
        if (String.class.isInstance(value)) {
            return createSingleMatcher(name, String.class.cast(value));
        } else if (Map.class.isInstance(value)) {
            return createCompositeMatcher(name, castToMap(value));
        } else if (TextContainer.class.isInstance(value)) {
            return createSingleTextMatcher(name, TextContainer.class.cast(value));
        } else {
            throw new IllegalArgumentException("unknown configuration :" + value);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> castToMap(Object value) {
        return Map.class.cast(value);
    }

    private RequestMatcher createSingleMatcher(String name, String value) {
        try {
            Method method = Moco.class.getMethod(name, String.class);
            Object result = method.invoke(null, value);
            return by(Resource.class.cast(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestMatcher createSingleTextMatcher(String name, TextContainer container) {
        if (container.isRawText()) {
            return createSingleMatcher(name, container.getText());
        }

        try {
            Method method = Moco.class.getMethod(name, String.class);
            Object result = method.invoke(null, container.getText());
            Method operationMethod = Moco.class.getMethod(container.getOperation(), Resource.class);
            return RequestMatcher.class.cast(operationMethod.invoke(null, result));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestMatcher createCompositeMatcher(String name, Map<String, String> collection) {
        return wrapRequestMatcher(null, transform(collection.entrySet(), toTargetMatcher(getMethodForCompositeMatcher(name))));
    }

    private Function<Map.Entry<String, String>, RequestMatcher> toTargetMatcher(final Method method) {
        return new Function<Map.Entry<String, String>, RequestMatcher>() {
            @Override
            public RequestMatcher apply(Map.Entry<String, String> pair) {
                try {
                    RequestExtractor extractor = RequestExtractor.class.cast(method.invoke(null, pair.getKey()));
                    return eq(extractor, pair.getValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        };
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
