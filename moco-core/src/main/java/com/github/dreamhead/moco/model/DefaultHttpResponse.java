package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpResponse;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static com.github.dreamhead.moco.model.MessageContent.content;

@JsonDeserialize(builder = DefaultHttpResponse.Builder.class)
public final class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {
    private final int status;

    public DefaultHttpResponse(final HttpProtocolVersion version, final int status,
                               final ImmutableMap<String, String[]> headers,
                               final MessageContent content) {
        super(version, content, headers);
        this.status = status;
    }

    @Override
    public int getStatus() {
        return status;
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("status", this.status);
    }

    public static HttpResponse newResponse(final FullHttpResponse response) {
        return builder()
                .withVersion(toHttpProtocolVersion(response.protocolVersion()))
                .withStatus(response.status().code())
                .withHeaders(toHeaders(response))
                .withContent(content()
                        .withContent(new ByteBufInputStream(response.content()))
                        .build())
                .build();
    }

    private static HttpProtocolVersion toHttpProtocolVersion(final HttpVersion httpVersion) {
        return HttpProtocolVersion.versionOf(httpVersion.text());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends DefaultHttpMessage.Builder<Builder> {
        private int status;

        public Builder withStatus(final int code) {
            this.status = code;
            return this;
        }

        public DefaultHttpResponse build() {
            return new DefaultHttpResponse(this.getVersion(), status, this.getHeaders(), this.getContent());
        }
    }
}
