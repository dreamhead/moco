package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.Extractors;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.parser.RequestMatcherFactory;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.FluentIterable.from;

public class DynamicRequestMatcherFactory extends Dynamics implements RequestMatcherFactory {

    @Override
    public RequestMatcher createRequestMatcher(final RequestSetting request) {
        return wrapRequestMatcher(request, createRequestMatchers(request));
    }

    private ImmutableList<RequestMatcher> createRequestMatchers(final RequestSetting request) {
        return from(getFields(RequestSetting.class)).filter(isValidField(request)).transform(fieldToRequestMatcher(request)).toList();
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

    private RequestMatcher createRequestMatcherFromValue(String name, Object value) {
        if ("json".equalsIgnoreCase(name)) {
            return json(text(Jsons.toJson(value)));
        }

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
        return invokeTarget(name, value, Resource.class);
    }

    private RequestMatcher createSingleTextMatcher(String name, TextContainer container) {
        if (container.isRawText()) {
            return createSingleMatcher(name, container.getText());
        }

        if ("exist".equals(container.getOperation())) {
            return existMatcher(Extractors.extractor(name), container);
        }

        return createRequestMatcherWithResource(container.getOperation(), createResource(name, container.getText()));
    }

    private RequestMatcher createRequestMatcherWithResource(String operation, Resource resource) {
        try {
            Method operationMethod = Moco.class.getMethod(operation, Resource.class);
            return RequestMatcher.class.cast(operationMethod.invoke(null, resource));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestMatcher createCompositeMatcher(String name, Map<String, Object> collection) {
        ImmutableList<RequestMatcher> matchers = from(collection.entrySet()).transform(toTargetMatcher(getExtractorMethod(name))).toList();
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

        if ("exist".equals(container.getOperation())) {
            return existMatcher(extractor, container);
        }

        try {
            Method operationMethod = Moco.class.getMethod(container.getOperation(), RequestExtractor.class, String.class);
            Object result = operationMethod.invoke(null, extractor, container.getText());
            return RequestMatcher.class.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> RequestMatcher existMatcher(RequestExtractor<T> extractor, TextContainer container) {
        if ("true".equals(container.getText())) {
            return exist(extractor);
        }

        if ("false".equals(container.getText())) {
            return not(exist(extractor));
        }

        throw new RuntimeException(String.format("Unknown exist parameter: [%s]", container.getText()));
    }

    private static RequestMatcher wrapRequestMatcher(RequestSetting request, ImmutableList<RequestMatcher> matchers) {
        switch (matchers.size()) {
            case 0:
                throw new IllegalArgumentException("illegal request setting:" + request);
            case 1:
                return matchers.get(0);
            default:
                return new AndRequestMatcher(matchers);
        }
    }
}
