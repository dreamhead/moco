package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

import java.util.Map;

public class RequestSetting {
    private String text;
    private String uri;
    private String file;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> xpaths;

    public String getUri() {
        return uri;
    }

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getXpaths() {
        return xpaths;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("URI", uri).add("text", text).add("file", file).add("headers", headers).toString();
    }
}
