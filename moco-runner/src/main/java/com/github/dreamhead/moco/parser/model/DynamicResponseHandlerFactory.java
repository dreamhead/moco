package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.parser.ResponseHandlerFactory;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.attachment;
import static com.github.dreamhead.moco.Moco.latency;
import static com.github.dreamhead.moco.Moco.proxy;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.toJson;
import static com.github.dreamhead.moco.Moco.var;
import static com.github.dreamhead.moco.Moco.version;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.ImmutableSet.of;
import static java.lang.String.format;

public class DynamicResponseHandlerFactory extends Dynamics implements ResponseHandlerFactory {
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
        return getResponseHandler(handlers.toList());
    }

    private ResponseHandler getResponseHandler(final ImmutableList<ResponseHandler> list) {
        if (list.size() == 1) {
            return list.get(0);
        }

        return and(list);
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
            return toJson(value);
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
            return with(latency(container.getLatency(), container.getUnit()));
        }

        if (ProxyContainer.class.isInstance(value)) {
            return createProxy((ProxyContainer) value);
        }

        if ("attachment".equalsIgnoreCase(name)) {
            AttachmentSetting attachment = (AttachmentSetting) value;
            return attachment(attachment.getFilename(), resourceFrom(attachment));
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
                return resourceFrom(resource, (TextContainer) field.get(resourceSetting));
            } catch (Exception ignored) {
            }
        }

        throw new IllegalArgumentException("resourceSetting is expected");
    }

    private ResponseHandler createCompositeHandler(final String name, final Map<String, TextContainer> map) {
        ImmutableList<ResponseHandler> handlers = from(map.entrySet()).transform(toTargetHandler(name)).toList();
        return getResponseHandler(handlers);
    }

    private Function<Map.Entry<String, TextContainer>, ResponseHandler> toTargetHandler(final String name) {
        return new Function<Map.Entry<String, TextContainer>, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final Map.Entry<String, TextContainer> pair) {
                String result = COMPOSITES.get(name);
                if (result == null) {
                    throw new RuntimeException("unknown composite handler name [" + name + "]");
                }

                return createResponseHandler(pair, result);
            }
        };
    }

    private ResponseHandler createResponseHandler(final Map.Entry<String, TextContainer> pair,
                                                  final String targetMethodName) {
        TextContainer container = pair.getValue();
        try {
            if (container.isForTemplate()) {
                Method method = Moco.class.getMethod(targetMethodName, String.class, Resource.class);
                return (ResponseHandler) method.invoke(null, pair.getKey(), template(container.getText()));
            }

            Method method = Moco.class.getMethod(targetMethodName, String.class, String.class);
            return (ResponseHandler) method.invoke(null, pair.getKey(), container.getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, TextContainer> castToMap(final Object value) {
        return Map.class.cast(value);
    }

    private Resource resourceFrom(final String name, final TextContainer container) {
        if (container.isRawText()) {
            return invokeTarget(name, container.getText(), Resource.class);
        }

        if (container.isForTemplate()) {
            if ("version".equalsIgnoreCase(name)) {
                return version(template(container.getText()));
            }

            return createTemplate(name, container);
        }

        if (container.isFileContainer()) {
            Optional<Resource> resource = fileResource(name, container);
            if (resource.isPresent()) {
                return resource.get();
            }
        }

        throw new IllegalArgumentException(format("unknown operation [%s]", container.getOperation()));
    }

    private Optional<Resource> fileResource(final String name, final TextContainer container) {
        FileContainer fileContainer = FileContainer.class.cast(container);
        TextContainer filename = fileContainer.getName();
        if (filename.isRawText()) {
            return Optional.of(invokeTarget(name, fileContainer.getName().getText(), fileContainer.getCharset(),
                    Resource.class, String.class, Optional.class));
        }

        if (filename.isForTemplate()) {
            return Optional.of(invokeTarget(name, createTemplate("text", filename), fileContainer.getCharset(),
                    Resource.class, Resource.class, Optional.class));
        }

        return Optional.absent();
    }

    private Resource createTemplate(final String name, final TextContainer container) {
        if (container.hasProperties()) {
            return template(invokeTarget(name, container.getText(), ContentResource.class),
                    toVariables(container.getProps()));
        }

        return template(invokeTarget(name, container.getText(), ContentResource.class));
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

    private ResponseHandler createProxy(final ProxyContainer proxy) {
        Failover failover = proxy.getFailover();

        if (proxy.hasProxyConfig()) {
            return proxy(proxy.getProxyConfig(), failover);
        }

        return proxy(proxy.getUrl(), failover);
    }
}
