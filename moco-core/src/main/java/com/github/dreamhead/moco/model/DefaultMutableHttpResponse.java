package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

public final class DefaultMutableHttpResponse implements MutableHttpResponse {
    private HttpProtocolVersion version;
    private Map<String, String[]> headers = Maps.newHashMap();
    private int status;
    private MessageContent content;

    private DefaultMutableHttpResponse() {
    }

    @Override
    public void setVersion(final HttpProtocolVersion version) {
        this.version = version;
    }

    @Override
    public void setStatus(final int status) {
        this.status = status;
    }

    @Override
    public void setContent(final MessageContent content) {
        if (this.content != null) {
            throw new IllegalArgumentException("Content has been set");
        }

        this.content = content;
    }

    private static final String[] SINGLE_VALUE_HEADERS = new String[] {
            HttpHeaders.CONTENT_TYPE
    };

    @Override
    public void addHeader(final String name, final Object value) {
        if (this.headers.containsKey(name) && isSingleValueHeader(name)) {
            this.headers.remove(name);
        }

        doAddHeader(name, value);
    }

    private boolean isSingleValueHeader(final String name) {
        for (String header : SINGLE_VALUE_HEADERS) {
            if (header.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    private void doAddHeader(final String name, final Object value) {
        this.headers.put(name, newValues(name, value));
    }

    private String[] newValues(final String name, final Object value) {
        if (this.headers.containsKey(name)) {
            String[] values = this.headers.get(name);
            String[] newValues = new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            newValues[values.length] = value.toString();
            return newValues;
        }

        return new String[]{value.toString()};
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
    public HttpProtocolVersion getVersion() {
        return this.version;
    }

    @Override
    public ImmutableMap<String, String[]> getHeaders() {
        return ImmutableMap.copyOf(this.headers);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }

    public static DefaultMutableHttpResponse newResponse(final HttpRequest request, final int status) {
        DefaultMutableHttpResponse httpResponse = new DefaultMutableHttpResponse();
        httpResponse.version = request.getVersion();
        httpResponse.status = status;
        return httpResponse;
    }

    public FullHttpResponse toFullResponse() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.valueOf(this.version.text()),
                HttpResponseStatus.valueOf(this.status));

        for (Map.Entry<String, String[]> entry : getHeaders().entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                response.headers().add(key, value);
            }
        }

        if (this.content != null) {
            response.content().writeBytes(this.content.getContent());
        }

        return response;
    }
}
