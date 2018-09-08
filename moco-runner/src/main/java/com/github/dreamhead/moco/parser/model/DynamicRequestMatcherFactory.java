package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.Extractors;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.parser.RequestMatcherFactory;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.not;
import static com.google.common.collect.FluentIterable.from;

public final class DynamicRequestMatcherFactory extends Dynamics implements RequestMatcherFactory {

    @Override
    public RequestMatcher createRequestMatcher(final RequestSetting request) {
        return wrapRequestMatcher(request, createRequestMatchers(request));
    }

    private ImmutableList<RequestMatcher> createRequestMatchers(final RequestSetting request) {
        return from(getFields(RequestSetting.class))
                .filter(isValidField(request))
                .transform(fieldToRequestMatcher(request))
                .toList();
    }

    private Function<Field, RequestMatcher> fieldToRequestMatcher(final RequestSetting request) {
        return new Function<Field, RequestMatcher>() {
            @Override
            public RequestMatcher apply(final Field field) {
                try {
                    Object value = field.get(request);
                    return createRequestMatcherFromValue(field.getName(), value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private RequestMatcher createRequestMatcherFromValue(final String name, final Object value) {
        if ("json".equalsIgnoreCase(name)) {
            return by(Moco.json(value));
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
    private Map<String, Object> castToMap(final Object value) {
        return Map.class.cast(value);
    }

    private RequestMatcher createSingleMatcher(final String name, final String value) {
        return by(createResource(name, value));
    }

    private Resource createResource(final String name, final String value) {
        return invokeTarget(name, value, Resource.class);
    }

    private RequestMatcher createSingleTextMatcher(final String name, final TextContainer container) {
        if (container.isRawText()) {
            return createSingleMatcher(name, container.getText());
        }

        if (isExistOperator(container)) {
            return existMatcher(Extractors.extractor(name), container);
        }

        return createRequestMatcherWithResource(container.getOperation(), createResource(name, container.getText()));
    }

    private boolean isExistOperator(final TextContainer container) {
        return "exist".equals(container.getOperation());
    }

    private RequestMatcher createRequestMatcherWithResource(final String operation, final Resource resource) {
        try {
            Method operationMethod = Moco.class.getMethod(operation, Resource.class);
            Object result = operationMethod.invoke(null, resource);
            Optional<RequestMatcher> matcher = createRequestMatcher(result);
            if (matcher.isPresent()) {
                return matcher.get();
            }

            throw new IllegalArgumentException("unknown operation [" + operation + "]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<RequestMatcher> createRequestMatcher(final Object result) {
        if (RequestMatcher.class.isInstance(result)) {
            return Optional.of(RequestMatcher.class.cast(result));
        }

        if (ContentResource.class.isInstance(result)) {
            return Optional.of(by(ContentResource.class.cast(result)));
        }

        return Optional.absent();
    }

    private RequestMatcher createCompositeMatcher(final String name, final Map<String, Object> collection) {
        ImmutableList<RequestMatcher> matchers = from(collection.entrySet())
                .transform(toTargetMatcher(getExtractorMethod(name)))
                .toList();
        return wrapRequestMatcher(null, matchers);
    }

    private Function<Map.Entry<String, Object>, RequestMatcher> toTargetMatcher(final Method extractorMethod) {
        return new Function<Map.Entry<String, Object>, RequestMatcher>() {
            @Override
            @SuppressWarnings("unchecked")
            public RequestMatcher apply(final Map.Entry<String, Object> pair) {
                RequestExtractor extractor = createRequestExtractor(extractorMethod, pair.getKey());
                return createRequestMatcher(extractor, pair.getValue());
            }
        };
    }

    private <T> RequestMatcher createRequestMatcher(final RequestExtractor<T> extractor, final Object value) {
        if (TextContainer.class.isInstance(value)) {
            return getRequestMatcher(extractor, TextContainer.class.cast(value));
        }

        throw new IllegalArgumentException("unknown value type: " + value);
    }

    private <T> RequestMatcher getRequestMatcher(final RequestExtractor<T> extractor, final TextContainer container) {
        if (container.isRawText()) {
            return eq(extractor, container.getText());
        }

        if (isExistOperator(container)) {
            return existMatcher(extractor, container);
        }

        try {
            Method operatorMethod = Moco.class.getMethod(container.getOperation(),
                    RequestExtractor.class, String.class);
            Object result = operatorMethod.invoke(null, extractor, container.getText());
            return RequestMatcher.class.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> RequestMatcher existMatcher(final RequestExtractor<T> extractor, final TextContainer container) {
        String text = container.getText();
        if ("true".equalsIgnoreCase(text)) {
            return exist(extractor);
        }

        if ("false".equalsIgnoreCase(text)) {
            return not(exist(extractor));
        }

        throw new IllegalArgumentException(String.format("Unknown exist parameter: [%s]", text));
    }

    private static RequestMatcher wrapRequestMatcher(final RequestSetting request,
                                                     final ImmutableList<RequestMatcher> matchers) {
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
