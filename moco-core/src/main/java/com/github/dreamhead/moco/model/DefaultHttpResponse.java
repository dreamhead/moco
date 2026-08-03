package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.util.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;

import static com.github.dreamhead.moco.model.MessageContent.content;

@JsonDeserialize(builder = DefaultHttpResponse.Builder.class)
public final class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {
    private final int status;
    private final List<SseEvent> sseEvents;

    public DefaultHttpResponse(final HttpProtocolVersion version,
                               final int status,
                               final ImmutableMap<String, String[]> headers,
                               final MessageContent content,
                               final List<SseEvent> sseEvents) {
        super(version, content, headers);
        this.status = status;
        this.sseEvents = sseEvents;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public Iterable<SseEvent> getSseEvents() {
        return sseEvents;
    }

    protected ToStringHelper toStringHelper() {
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
        @JsonProperty("sseEvents")
        private List<SseEvent> sseEvents;

        public Builder withStatus(final int code) {
            this.status = code;
            return this;
        }

        @JsonProperty("sseEvents")
        public Builder withSseEvents(final List<SseEvent> events) {
            this.sseEvents = events;
            return this;
        }

        public DefaultHttpResponse build() {
            return new DefaultHttpResponse(this.getVersion(), status, this.getHeaders(), this.getContent(), sseEvents);
        }
    }
}
