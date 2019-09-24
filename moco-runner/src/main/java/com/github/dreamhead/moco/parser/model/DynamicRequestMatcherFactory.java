package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.Extractors;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.parser.RequestMatcherFactory;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.not;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public final class DynamicRequestMatcherFactory extends Dynamics implements RequestMatcherFactory {

    @Override
    public RequestMatcher createRequestMatcher(final RequestSetting request) {
        return wrapRequestMatcher(request, createRequestMatchers(request));
    }

    private List<RequestMatcher> createRequestMatchers(final RequestSetting request) {
        return StreamSupport.stream(getFields(RequestSetting.class).spliterator(), false)
                .filter(isValidField(request))
                .map(fieldToRequestMatcher(request))
                .collect(toList());
    }

    private Function<Field, RequestMatcher> fieldToRequestMatcher(final RequestSetting request) {
        return field -> {
            try {
                Object value = field.get(request);
                return createRequestMatcherFromValue(field.getName(), value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private RequestMatcher createRequestMatcherFromValue(final String name, final Object value) {
        if ("json".equalsIgnoreCase(name)) {
            return by(Moco.json(value));
        }

        if (value instanceof Map) {
            return createCompositeMatcher(name, castToMap(value));
        }

        if (value instanceof TextContainer) {
            return createSingleTextMatcher(name, (TextContainer) value);
        }

        throw new IllegalArgumentException("unknown configuration :" + value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(final Object value) {
        return (Map<String, Object>) value;
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
        if (result instanceof RequestMatcher) {
            return of((RequestMatcher) result);
        }

        if (result instanceof ContentResource) {
            return of(by((ContentResource) result));
        }

        return empty();
    }

    private RequestMatcher createCompositeMatcher(final String name, final Map<String, Object> collection) {
        List<RequestMatcher> matchers = collection.entrySet().stream()
                .map(toTargetMatcher(getExtractorMethod(name)))
                .collect(toList());
        return wrapRequestMatcher(null, matchers);
    }

    @SuppressWarnings("unchecked")
    private Function<Map.Entry<String, Object>, RequestMatcher> toTargetMatcher(final Method extractorMethod) {
        return pair -> {
            RequestExtractor extractor = createRequestExtractor(extractorMethod, pair.getKey());
            return createRequestMatcher(extractor, pair.getValue());
        };
    }

    private <T> RequestMatcher createRequestMatcher(final RequestExtractor<T> extractor, final Object value) {
        if (value instanceof TextContainer) {
            return getRequestMatcher(extractor, (TextContainer) value);
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
            return (RequestMatcher) result;
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
                                                     final List<RequestMatcher> matchers) {
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
