package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpResponse;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;

@JsonDeserialize(builder=DumpHttpResponse.Builder.class)
public class DumpHttpResponse extends DumpMessage implements HttpResponse {
    private final int statusCode;

    public DumpHttpResponse(String version, int statusCode, ImmutableMap<String, String> headers, String content) {
        super(version, content, headers);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static DumpHttpResponse newResponse(FullHttpResponse response) {
        ImmutableMap.Builder<String, String> headerBuilder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : response.headers()) {
            headerBuilder.put(entry);
        }

        return builder()
                .withVersion(response.getProtocolVersion().text())
                .withStatusCode(response.getStatus().code())
                .withHeaders(headerBuilder.build())
                .withContent(response.content().toString(Charset.defaultCharset()))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String version;
        private String content;
        private ImmutableMap<String, String> headers;
        private int statusCode;

        public Builder withVersion(String version) {
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

        public Builder withStatusCode(int code) {
            this.statusCode = code;
            return this;
        }

        public DumpHttpResponse build() {
            return new DumpHttpResponse(version, statusCode, headers, content);
        }
    }
}
