package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

import java.util.Map;

public class RequestSetting {
    private TextContainer text;
    private TextContainer uri;
    private TextContainer file;
    private TextContainer method;
    private TextContainer version;

    private Map<String, TextContainer> headers;
    private Map<String, TextContainer> xpaths;
    private Map<String, TextContainer> queries;
    private Map<String, TextContainer> cookies;

    public Object getUri() {
        return uri;
    }

    public TextContainer getText() {
        return text;
    }

    public TextContainer getFile() {
        return file;
    }

    public TextContainer getMethod() {
        return method;
    }

    public TextContainer getVersion() {
        return version;
    }

    public Map<String, TextContainer> getCookies() {
        return cookies;
    }

    public Map<String, TextContainer> getHeaders() {
        return headers;
    }

    public Map<String, TextContainer> getXpaths() {
        return xpaths;
    }

    public Map<String, TextContainer> getQueries() {
        return queries;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("URI", uri).add("text", text).add("file", file).add("headers", headers).toString();
    }
}
