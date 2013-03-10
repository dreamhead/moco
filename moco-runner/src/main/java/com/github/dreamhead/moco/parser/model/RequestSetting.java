package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

import java.util.Map;

public class RequestSetting {
    private TextContainer text;
    private TextContainer uri;
    private String file;
    private TextContainer method;

    private Map<String, String> headers;
    private Map<String, String> xpaths;
    private Map<String, String> queries;
    private Map<String, String> cookies;

    public Object getUri() {
        return uri;
    }

    public TextContainer getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    public TextContainer getMethod() {
        return method;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getXpaths() {
        return xpaths;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("URI", uri).add("text", text).add("file", file).add("headers", headers).toString();
    }
}
