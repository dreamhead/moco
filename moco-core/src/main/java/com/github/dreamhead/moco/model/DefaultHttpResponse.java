package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpResponse;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.collect.ImmutableMap.copyOf;

@JsonDeserialize(builder = DefaultHttpResponse.Builder.class)
public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {
    private final int status;

    public DefaultHttpResponse(final HttpProtocolVersion version, final int status,
                               final ImmutableMap<String, String> headers,
                               final MessageContent content) {
        super(version, content, headers);
        this.status = status;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public static DefaultHttpResponse newResponse(final FullHttpResponse response) {
        ImmutableMap.Builder<String, String> headerBuilder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : response.headers()) {
            headerBuilder.put(entry);
        }

        return builder()
                .withVersion(HttpProtocolVersion.versionOf(response.getProtocolVersion().text()))
                .withStatus(response.getStatus().code())
                .withHeaders(headerBuilder.build())
                .withContent(content()
                        .withContent(new ByteBufInputStream(response.content()))
                        .build())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private HttpProtocolVersion version;
        private MessageContent content;
        private ImmutableMap<String, String> headers;
        private int status;

        public Builder withVersion(final HttpProtocolVersion version) {
            this.version = version;
            return this;
        }

        public Builder withContent(final MessageContent content) {
            this.content = content;
            return this;
        }

        public Builder withHeaders(final Map<String, String> headers) {
            if (headers != null) {
                this.headers = copyOf(headers);
            }

            return this;
        }

        public Builder withStatus(final int code) {
            this.status = code;
            return this;
        }

        public DefaultHttpResponse build() {
            return new DefaultHttpResponse(version, status, headers, content);
        }
    }
}
