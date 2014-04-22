package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpResponse;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;

@JsonDeserialize(builder=DefaultHttpResponse.Builder.class)
public class DefaultHttpResponse implements HttpResponse {
    private final HttpProtocolVersion version;
    private final String content;
    private final ImmutableMap<String, String> headers;
    private final int status;

    public DefaultHttpResponse(HttpProtocolVersion version, int status, ImmutableMap<String, String> headers, String content) {
        this.version = version;
        this.headers = headers;
        this.content = content;
        this.status = status;
    }

    public HttpProtocolVersion getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public ImmutableMap<String, String> getHeaders() {
        return this.headers;
    }

    public int getStatus() {
        return status;
    }

    public static DefaultHttpResponse newResponse(FullHttpResponse response) {
        ImmutableMap.Builder<String, String> headerBuilder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : response.headers()) {
            headerBuilder.put(entry);
        }

        return builder()
                .withVersion(HttpProtocolVersion.versionOf(response.getProtocolVersion().text()))
                .withStatus(response.getStatus().code())
                .withHeaders(headerBuilder.build())
                .withContent(response.content().toString(Charset.defaultCharset()))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private HttpProtocolVersion version;
        private String content;
        private ImmutableMap<String, String> headers;
        private int status;

        public Builder withVersion(HttpProtocolVersion version) {
            this.version = version;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withHeaders(Map<String, String> headers) {
            if (headers != null) {
                this.headers = copyOf(headers);
            }

            return this;
        }

        public Builder withStatus(int code) {
            this.status = code;
            return this;
        }

        public DefaultHttpResponse build() {
            return new DefaultHttpResponse(version, status, headers, content);
        }
    }
}
