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
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.*;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableSet.of;
import static java.lang.String.format;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseSetting {
    private static final ImmutableSet<String> RESOURCES = of("text", "file", "pathResource", "version");
    private static final ImmutableMap<String, String> COMPOSITES = ImmutableMap.<String,String>builder()
            .put("headers", "header")
            .put("cookies", "cookie")
            .build();

    private String status;
    private ProxyContainer proxy;
    private Map<String, String> headers;
    private Map<String, String> cookies;
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

    private Iterable<Field> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return Arrays.asList(fields);
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

    private <T> Predicate<Field> fieldExist(final T request) {
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

    public ResponseHandler getResponseHandler() {
        FluentIterable<ResponseHandler> handlers = from(getFields(this.getClass())).filter(and(not(or(isClassField(), isFinalField())), fieldExist(this))).transform(fieldToResponseHandler(this));
        List<ResponseHandler> list = handlers.toList();
        return getResponseHandler(list);
    }

    private ResponseHandler getResponseHandler(List<ResponseHandler> list) {
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
            return responseHandler(resourceFrom(name, container));
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
            return createProxy((ProxyContainer)value);
        }

        throw new IllegalArgumentException(format("unknown field [%s]", name));
    }

    private ResponseHandler createCompositeHandler(String name, Map<String, String> map) {
        List<ResponseHandler> handlers = from(map.entrySet()).transform(toTargetHandler(getMethodForCompositeHandler(name))).toList();
        return getResponseHandler(handlers);
    }

    private Function<Map.Entry<String, String>, ResponseHandler> toTargetHandler(final Method method) {
        return new Function<Map.Entry<String, String>, ResponseHandler>() {
            @Override
            public ResponseHandler apply(Map.Entry<String, String> pair) {
                try {
                    return (ResponseHandler)method.invoke(null, pair.getKey(), pair.getValue());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Method getMethodForCompositeHandler(String name) {
        try {
            String result = COMPOSITES.get(name);
            if (result == null) {
                throw new RuntimeException("unknown composite handler name [" + name + "]");
            }
            return Moco.class.getMethod(result, String.class, String.class);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> castToMap(Object value) {
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

    private <T> T invokeTarget(String name, Object value, Class<T> clazz) {
        try {
            Method method = Moco.class.getMethod(name, value.getClass());
            Object result = method.invoke(null, value);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T invokeTarget(String name, int value, Class<T> clazz) {
        try {
            Method method = Moco.class.getMethod(name, Integer.TYPE);
            Object result = method.invoke(null, value);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T invokeTarget(String name, long value, Class<T> clazz) {
        try {
            Method method = Moco.class.getMethod(name, Long.TYPE);
            Object result = method.invoke(null, value);
            return clazz.cast(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private ResponseHandler createProxy(ProxyContainer proxy) {
        if (proxy.getFailover() != null) {
            return proxy(proxy.getUrl(), failover(proxy.getFailover()));
        }

        return proxy(proxy.getUrl());
    }
}
