package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.DynamicRequestMatcherFactory;
import com.github.dreamhead.moco.parser.RequestMatcherFactory;
import com.google.common.base.Objects;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RequestSetting {
    private final RequestMatcherFactory factory = new DynamicRequestMatcherFactory();

    private TextContainer text;
    private TextContainer uri;
    private TextContainer file;
    @JsonProperty("path_resource")
    private TextContainer pathResource;
    private TextContainer method;
    private TextContainer version;

    private Map<String, TextContainer> headers;
    private Map<String, TextContainer> xpaths;
    @JsonProperty("json_paths")
    private Map<String, TextContainer> jsonPaths;
    private Map<String, TextContainer> queries;
    private Map<String, TextContainer> cookies;
    private Map<String, TextContainer> forms;

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

    public RequestMatcher getRequestMatcher() {
        return factory.createRequestMatcher(this);
    }
}
