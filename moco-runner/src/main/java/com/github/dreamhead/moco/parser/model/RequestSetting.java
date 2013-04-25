package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.Map;

public class RequestSetting {
    private TextContainer text;
    private TextContainer uri;
    private TextContainer file;
    @JsonProperty("path_resource")
    private TextContainer pathResource;
    private TextContainer method;
    private TextContainer version;

    private Map<String, TextContainer> headers;
    private Map<String, TextContainer> xpaths;
    private Map<String, TextContainer> queries;
    private Map<String, TextContainer> cookies;
    private Map<String, TextContainer> forms;

    public TextContainer getUri() {
        return uri;
    }

    public TextContainer getText() {
        return text;
    }

    public TextContainer getFile() {
        return file;
    }

    public TextContainer getPathResource() {
        return pathResource;
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

    public Map<String, TextContainer> getForms() {
        return forms;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("version", version)
                .add("URI", uri)
                .add("text", text)
                .add("file", file)
                .add("headers", headers)
                .add("xpaths", xpaths)
                .add("queries", queries)
                .add("cookies", cookies)
                .add("forms", forms)
                .toString();
    }
}
