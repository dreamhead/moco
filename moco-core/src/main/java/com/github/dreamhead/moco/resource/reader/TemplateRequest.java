package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.internal.ContextKey;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.AntPathMatcher;
import com.github.dreamhead.moco.util.Jsons;
import com.github.dreamhead.moco.util.Xmls;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.github.dreamhead.moco.util.Maps.arrayValueToSimple;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

public final class TemplateRequest {
    private final Request request;
    private final SessionContext context;

    public TemplateRequest(final SessionContext context) {
        this.request = context.getRequest();
        this.context = context;
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
            ImmutableMap<String, String[]> queries = httpRequest.getQueries();
            return queries.entrySet().stream()
                    .collect(toImmutableMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
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

    public Object getXml() {
        try {
            return Xmls.toObject(this.request.getContent().toString(), Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Xml content is expected", e);
        }
    }

    public Map<String, String> getPath() {
        if (context == null) {
            throw new IllegalArgumentException("Uri path is expected");
        }

        final Resource resource = this.context.get(ContextKey.PATH, Resource.class);
        if (resource == null) {
            throw new IllegalArgumentException("Uri path is expected");
        }

        final MessageContent content = resource.readFor(this.context);
        final String path = content.toString();
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.extractUriTemplateVariables(path, this.getUri());
    }

    public TemplateClient getClient() {
        return new TemplateClient(this.request.getClientAddress());
    }

    public static class TemplateClient {
        private final String address;

        public TemplateClient(final String address) {
            this.address = address;
        }

        public final String getAddress() {
            return address;
        }
    }
}
