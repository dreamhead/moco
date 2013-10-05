package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Objects;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@JsonDeserialize(builder=DumpHttpRequest.Builder.class)
public class DumpHttpRequest extends DumpMessage implements HttpRequest {
    private final Map<String, String> queries;
    private final String method;
    private final String uri;

    private DumpHttpRequest(String version, String content, String method, String uri,
                            Map<String, String> headers, Map<String, String> queries) {
        super(version, content, headers);
        this.method = method;
        this.uri = uri;
        this.queries = queries;
    }

    @Override
    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), queries, method, uri);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(DumpHttpRequest.class)
                .omitNullValues()
                .add("uri", uri)
                .add("version", version)
                .add("queries", queries)
                .add("method", method)
                .add("headers", headers)
                .add("content", content)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String version;
        private String content;
        private Map<String, String> headers = newHashMap();
        private Map<String, String> queries = newHashMap();
        private String method;
        private String uri;

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder withQueries(Map<String, String> queries) {
            this.queries = queries;
            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public DumpHttpRequest build() {
            return new DumpHttpRequest(version, content, method, uri, headers, queries);
        }
    }
}
