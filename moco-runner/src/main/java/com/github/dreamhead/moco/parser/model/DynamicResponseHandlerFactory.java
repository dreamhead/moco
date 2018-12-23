package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.CookieAttribute;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.ResponseHandlerFactory;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.attachment;
import static com.github.dreamhead.moco.Moco.json;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.var;
import static com.github.dreamhead.moco.Moco.version;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.tail;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.ImmutableSet.of;
import static java.lang.String.format;

public final class DynamicResponseHandlerFactory extends Dynamics implements ResponseHandlerFactory {
    private static final ImmutableSet<String> RESOURCES = of("text", "file", "pathResource", "version");
    private static final ImmutableMap<String, String> COMPOSITES = ImmutableMap.<String, String>builder()
            .put("headers", "header")
            .put("cookies", "cookie")
            .build();

    @Override
    public ResponseHandler createResponseHandler(final ResponseSetting responseSetting) {
        FluentIterable<ResponseHandler> handlers = from(getFields(responseSetting.getClass()))
                .filter(isValidField(responseSetting))
                .transform(fieldToResponseHandler(responseSetting));
        return getResponseHandler(handlers);
    }

    private ResponseHandler getResponseHandler(final FluentIterable<ResponseHandler> handlers) {
        if (handlers.size() == 1) {
            return handlers.get(0);
        }

        return and(handlers);
    }

    private boolean isResource(final String name) {
        return RESOURCES.contains(name);
    }

    private Function<Field, ResponseHandler> fieldToResponseHandler(final ResponseSetting response) {
        return new Function<Field, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final Field field) {
                try {
                    Object value = field.get(response);
                    return createResponseHandler(field.getName(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private ResponseHandler createResponseHandler(final String name, final Object value) {
        if ("json".equalsIgnoreCase(name)) {
            return with(json(value));
        }

        if (isResource(name) && TextContainer.class.isInstance(value)) {
            TextContainer container = TextContainer.class.cast(value);
            return with(resourceFrom(name, container));
        }

        if (Map.class.isInstance(value)) {
            return createCompositeHandler(name, castToMap(value));
        }

        if ("status".equalsIgnoreCase(name)) {
            return status(Integer.parseInt(value.toString()));
        }

        if ("latency".equalsIgnoreCase(name)) {
            LatencyContainer container = LatencyContainer.class.cast(value);
            return with(container.asProcedure());
        }

        if (ProxyContainer.class.isInstance(value)) {
            return ((ProxyContainer) value).asResponseHandler();
        }

        if ("attachment".equalsIgnoreCase(name)) {
            AttachmentSetting attachment = AttachmentSetting.class.cast(value);
            return attachment(attachment.getFilename(), resourceFrom(attachment));
        }

        if ("seq".equalsIgnoreCase(name)) {
            CollectionContainer sequence = CollectionContainer.class.cast(value);
            ResponseHandler[] responseHandlers = sequence.toResponseHandlers();
            return Moco.seq(head(responseHandlers), tail(responseHandlers));
        }

        if ("cycle".equalsIgnoreCase(name)) {
            CollectionContainer sequence = CollectionContainer.class.cast(value);
            ResponseHandler[] responseHandlers = sequence.toResponseHandlers();
            return Moco.cycle(head(responseHandlers), tail(responseHandlers));
        }


        throw new IllegalArgumentException(format("unknown field [%s]", name));
    }

    private Field getField(final Class<?> clazz, final String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getField(superclass, name);
            }
            throw e;
        }
    }

    private Resource resourceFrom(final BaseResourceSetting resourceSetting) {
        for (String resource : RESOURCES) {
            try {
                Field field = getField(resourceSetting.getClass(), resource);
                field.setAccessible(true);
                return resourceFrom(resource, (TextContainer) field.get(resourceSetting));
            } catch (Exception ignored) {
            }
        }

        throw new IllegalArgumentException("resourceSetting is expected");
    }

    private ResponseHandler createCompositeHandler(final String name, final Map<String, Container> map) {
        FluentIterable<ResponseHandler> handlers = from(map.entrySet()).transform(toTargetHandler(name));
        return getResponseHandler(handlers);
    }

    private Function<Map.Entry<String, Container>, ResponseHandler> toTargetHandler(final String name) {
        return new Function<Map.Entry<String, Container>, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final Map.Entry<String, Container> pair) {
                String result = COMPOSITES.get(name);
                if (result == null) {
                    throw new IllegalArgumentException("unknown composite handler name [" + name + "]");
                }

                return createResponseHandler(pair, result);
            }
        };
    }

    private Resource getResource(final TextContainer container) {
        if (container.isForTemplate()) {
            return template(container.getText());
        }

        return text(container.getText());
    }

    private Resource getResource(final CookieContainer container) {
        if (container.isForTemplate()) {
            return template(container.getTemplate());
        }

        return text(container.getValue());
    }

    private ResponseHandler createResponseHandler(final Map.Entry<String, Container> pair,
                                                  final String targetMethodName) {
        Container container = pair.getValue();
        String key = pair.getKey();
        if (container instanceof TextContainer) {
            return createResponseHandler(targetMethodName, key, (TextContainer) container);
        }

        if (container instanceof CookieContainer) {
            return createCookieResponseHandler(targetMethodName, key, (CookieContainer) container);
        }

        throw new IllegalArgumentException();
    }

    private ResponseHandler createResponseHandler(final String target, final String key,
                                                  final TextContainer textContainer) {
        try {
            Method method = Moco.class.getMethod(target, String.class, Resource.class);
            return (ResponseHandler) method.invoke(null, key, getResource(textContainer));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseHandler createCookieResponseHandler(final String target, final String key,
                                                        final CookieContainer cookieContainer) {
        try {
            Method method = Moco.class.getMethod(target, String.class, Resource.class, CookieAttribute[].class);
            return (ResponseHandler) method.invoke(null, key,
                    getResource(cookieContainer), cookieContainer.getOptions());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Container> castToMap(final Object value) {
        return Map.class.cast(value);
    }

    private Resource resourceFrom(final String name, final TextContainer container) {
        if (container.isFileContainer()) {
            return fileResource(name, FileContainer.class.cast(container));
        }

        return textResource(name, container);
    }

    private Resource textResource(final String name, final TextContainer container) {
        if (container.isRawText()) {
            return invokeTarget(name, container.getText(), Resource.class);
        }

        if (container.isForTemplate()) {
            if ("version".equalsIgnoreCase(name)) {
                return version(container.asTemplateResource());
            }

            return container.asTemplateResource(name);
        }

        throw new IllegalArgumentException(format("unknown text container:[%s]", container));
    }

    private Resource fileResource(final String name, final FileContainer fileContainer) {
        if (fileContainer.isForTemplate()) {
            if ("version".equalsIgnoreCase(name)) {
                return version(fileContainer.asTemplateResource());
            }

            return fileContainer.asTemplateResource(name);
        }

        TextContainer filename = fileContainer.getName();
        if (filename.isRawText()) {
            return asResource(name, fileContainer);
        }

        if (filename.isForTemplate()) {
            Optional<Charset> charset = fileContainer.getCharset();
            Resource resource = filename.asTemplateResource();
            return asResource(name, resource, charset);
        }

        throw new IllegalArgumentException(format("unknown file container:[%s]", fileContainer));
    }

    private Resource asResource(final String name, final Resource resource, final Optional<Charset> charset) {
        if (charset.isPresent()) {
            return invokeTarget(name, resource, charset.get(),
                    Resource.class, Resource.class, Charset.class);
        }

        return invokeTarget(name, resource, Resource.class, Resource.class);
    }

    private Resource asResource(final String name, final FileContainer fileContainer) {
        Optional<Charset> charset = fileContainer.getCharset();
        String text = fileContainer.getName().getText();
        return asResource(name, text(text), charset);
    }

    public static ImmutableMap<String, RequestExtractor<?>> toVariables(final Map<String, TextContainer> props) {
        return copyOf(Maps.transformEntries(props, toVariable()));
    }

    private static Maps.EntryTransformer<String, TextContainer, RequestExtractor<?>> toVariable() {
        return new Maps.EntryTransformer<String, TextContainer, RequestExtractor<?>>() {
            @Override
            public RequestExtractor<?> transformEntry(final String key, final TextContainer value) {
                if (value.isRawText()) {
                    return var(value.getText());
                }

                return createRequestExtractor(getExtractorMethod(value.getOperation()), value.getText());
            }
        };
    }

}
