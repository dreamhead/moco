package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpMessage;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.util.Maps.iterableValueToArray;
import static com.github.dreamhead.moco.util.Maps.simpleValueToArray;
import static com.google.common.collect.ImmutableMap.copyOf;
import static java.util.Collections.EMPTY_MAP;

public abstract class DefaultHttpMessage implements HttpMessage {
    private final HttpProtocolVersion version;
    private final MessageContent content;
    private final ImmutableMap<String, String[]> headers;

    protected DefaultHttpMessage(final HttpProtocolVersion version,
                                 final MessageContent content,
                                 final ImmutableMap<String, String[]> headers) {
        this.version = version;
        this.content = content;
        this.headers = headers;
    }

    @Override
    public HttpProtocolVersion getVersion() {
        return this.version;
    }

    @Override
    public ImmutableMap<String, String[]> getHeaders() {
        return this.headers;
    }

    @Override
    public String getHeader(final String name) {
        if (!this.headers.containsKey(name)) {
            return null;
        }

        String[] values = this.headers.get(name);
        return values[0];
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }

    protected static Map<String, Iterable<String>> toHeaders(final io.netty.handler.codec.http.HttpMessage message) {
        return toHeaders(message.headers());
    }

    private static Map<String, Iterable<String>> toHeaders(final Iterable<Map.Entry<String, String>> httpHeaders) {
        Map<String, Iterable<String>> headers = new HashMap<>();
        for (Map.Entry<String, String> entry : httpHeaders) {
            String key = entry.getKey();
            List<String> values = getValues(headers, key);
            values.add(entry.getValue());
            headers.put(key, values);
        }

        return headers;
    }

    private static List<String> getValues(final Map<String, Iterable<String>> headers, final String key) {
        if (headers.containsKey(key)) {
            return (List<String>) headers.get(key);
        }

        return new ArrayList<>();
    }

    protected static abstract class Builder<T extends Builder> {
        private final Class<T> clazz;
        protected HttpProtocolVersion version;
        protected MessageContent content;
        protected ImmutableMap<String, String[]> headers;

        public Builder() {
            this.clazz = getRealClass();
        }

        @SuppressWarnings("unchecked")
        private Class<T> getRealClass() {
            return (Class<T>) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        }

        public T withVersion(final HttpProtocolVersion version) {
            this.version = version;
            return clazz.cast(this);
        }

        public T withContent(final String content) {
            this.content = content(content);
            return clazz.cast(this);
        }

        public T withContent(final MessageContent content) {
            this.content = content;
            return clazz.cast(this);
        }

        public T withHeaders(final Map<String, ?> headers) {
            if (headers != null) {
                this.headers = asHeaders(headers);
            }

            return clazz.cast(this);
        }

        @SuppressWarnings("unchecked")
        private ImmutableMap<String, String[]> asHeaders(final Map<String, ?> headers) {
            if (headers.isEmpty()) {
                return ImmutableMap.of();
            }

            Object value = Iterables.getFirst(headers.entrySet(), null).getValue();
            if (value instanceof String) {
                return simpleValueToArray((Map<String, String>)headers);
            }

            if (value instanceof String[]) {
                return copyOf((Map<String, String[]>)headers);
            }

            if (value instanceof Iterable) {
                return iterableValueToArray((Map<String, Iterable<String>>) headers);
            }

            throw new IllegalArgumentException("Unknown header value type [" + value.getClass() + "]");
        }
    }
}
