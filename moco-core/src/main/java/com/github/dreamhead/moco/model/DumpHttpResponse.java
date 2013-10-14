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
    private final int status;

    public DumpHttpResponse(String version, int status, ImmutableMap<String, String> headers, String content) {
        super(version, content, headers);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static DumpHttpResponse newResponse(FullHttpResponse response) {
        ImmutableMap.Builder<String, String> headerBuilder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : response.headers()) {
            headerBuilder.put(entry);
        }

        return builder()
                .withVersion(response.getProtocolVersion().text())
                .withStatus(response.getStatus().code())
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
        private int status;

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

        public Builder withStatus(int code) {
            this.status = code;
            return this;
        }

        public DumpHttpResponse build() {
            return new DumpHttpResponse(version, status, headers, content);
        }
    }
}
