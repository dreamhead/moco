package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableSet.of;
import static java.lang.String.format;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseSetting extends Dynamics {
    private static final ImmutableSet<String> RESOURCES = of("text", "file", "pathResource", "version");
    private static final ImmutableMap<String, String> COMPOSITES = ImmutableMap.<String, String>builder()
            .put("headers", "header")
            .put("cookies", "cookie")
            .build();

    private String status;
    private ProxyContainer proxy;
    private Map<String, TextContainer> headers;
    private Map<String, TextContainer> cookies;
    private Long latency;
    private TextContainer text;
    private TextContainer file;
    @JsonProperty("path_resource")
    private TextContainer pathResource;
    private TextContainer version;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("file", file)
                .add("version", version)
                .add("status", status)
                .add("headers", headers)
                .add("cookies", cookies)
                .add("proxy", proxy)
                .add("latency", latency)
                .toString();
    }

    public ResponseHandler getResponseHandler() {
        FluentIterable<ResponseHandler> handlers = from(getFields(this.getClass())).filter(isValidField(this)).transform(fieldToResponseHandler(this));
        return getResponseHandler(handlers.toList());
    }

    private ResponseHandler getResponseHandler(ImmutableList<ResponseHandler> list) {
        if (list.size() == 1) {
            return list.get(0);
        }

        return new AndResponseHandler(list);
    }

    private boolean isResource(String name) {
        return RESOURCES.contains(name);
    }

    private Function<Field, ResponseHandler> fieldToResponseHandler(final ResponseSetting response) {
        return new Function<Field, ResponseHandler>() {
            @Override
            public ResponseHandler apply(Field field) {
                try {
                    Object value = field.get(response);
                    return createResponseHandler(field.getName(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private ResponseHandler createResponseHandler(String name, Object value) {
        if (isResource(name) && TextContainer.class.isInstance(value)) {
            TextContainer container = TextContainer.class.cast(value);
            return with(resourceFrom(name, container));
        }

        if (Map.class.isInstance(value)) {
            return createCompositeHandler(name, castToMap(value));
        }


        if ("status".equalsIgnoreCase(name)) {
            return invokeTarget(name, Integer.parseInt(value.toString()), ResponseHandler.class);
        }

        if ("latency".equalsIgnoreCase(name)) {
            return invokeTarget(name, Long.parseLong(value.toString()), ResponseHandler.class);
        }

        if (ProxyContainer.class.isInstance(value)) {
            return createProxy((ProxyContainer) value);
        }

        throw new IllegalArgumentException(format("unknown field [%s]", name));
    }

    private ResponseHandler createCompositeHandler(String name, Map<String, TextContainer> map) {
        ImmutableList<ResponseHandler> handlers = from(map.entrySet()).transform(toTargetHandler(name)).toList();
        return getResponseHandler(handlers);
    }

    private Function<Map.Entry<String, TextContainer>, ResponseHandler> toTargetHandler(final String name) {
        return new Function<Map.Entry<String, TextContainer>, ResponseHandler>() {
            @Override
            public ResponseHandler apply(Map.Entry<String, TextContainer> pair) {
                String result = COMPOSITES.get(name);
                if (result == null) {
                    throw new RuntimeException("unknown composite handler name [" + name + "]");
                }

                return createResponseHandler(pair, result);
            }
        };
    }

    private ResponseHandler createResponseHandler(Map.Entry<String, TextContainer> pair, String targetMethodName) {
        TextContainer container = pair.getValue();
        try {
            if ("template".equalsIgnoreCase(container.getOperation())) {
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
    private Map<String, TextContainer> castToMap(Object value) {
        return Map.class.cast(value);
    }

    private Resource resourceFrom(String name, TextContainer container) {
        if (container.isRawText()) {
            return invokeTarget(name, container.getText(), Resource.class);
        }

        if ("template".equalsIgnoreCase(container.getOperation())) {
            if ("version".equalsIgnoreCase(name)) {
                return version(template(container.getText()));
            }

            return template(invokeTarget(name, container.getText(), ContentResource.class));
        }

        throw new IllegalArgumentException(format("unknown operation [%s]", container.getOperation()));
    }

    private ResponseHandler createProxy(ProxyContainer proxy) {
        if (proxy.getFailover() != null) {
            return proxy(proxy.getUrl(), failover(proxy.getFailover()));
        }

        return proxy(proxy.getUrl());
    }
}
