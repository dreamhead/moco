package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.github.dreamhead.moco.util.Maps.arrayValueToSimple;

public final class TemplateRequest {
    private Request request;

    public TemplateRequest(final Request request) {
        this.request = request;
    }

    public MessageContent getContent() {
        return this.request.getContent();
    }

    public HttpProtocolVersion getVersion() {
        if (this.request instanceof HttpRequest) {
            return ((HttpRequest) this.request).getVersion();
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public Map<String, String> getHeaders() {
        if (this.request instanceof HttpRequest) {
            return arrayValueToSimple(((HttpRequest) this.request).getHeaders());
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public String getUri() {
        if (this.request instanceof HttpRequest) {
            return ((HttpRequest) this.request).getUri();
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public String getMethod() {
        if (this.request instanceof HttpRequest) {
            return ((HttpRequest) this.request).getMethod().name();
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public ImmutableMap<String, String> getQueries() {
        if (this.request instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) this.request;
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            ImmutableMap<String, String[]> queries = httpRequest.getQueries();
            for (String key : queries.keySet()) {
                builder.put(key, queries.get(key)[0]);
            }

            return builder.build();
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public ImmutableMap<String, String> getForms() {
        if (this.request instanceof DefaultHttpRequest) {
            return ((DefaultHttpRequest) this.request).getForms();
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public ImmutableMap<String, String> getCookies() {
        if (this.request instanceof DefaultHttpRequest) {
            return ((DefaultHttpRequest) this.request).getCookies();
        }

        throw new IllegalArgumentException("Request is not HTTP request");
    }

    public Object getJson() {
        try {
            return Jsons.toObject(this.request.getContent().toString(), Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Json content is expected", e);
        }
    }
}
