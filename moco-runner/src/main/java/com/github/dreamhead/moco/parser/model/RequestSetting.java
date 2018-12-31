package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.RequestMatcherFactory;
import com.google.common.base.MoreObjects;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class RequestSetting extends BaseResourceSetting {
    private final RequestMatcherFactory factory = new DynamicRequestMatcherFactory();

    private TextContainer uri;

    private TextContainer method;
    private TextContainer version;

    private Map<String, TextContainer> headers;
    private Map<String, TextContainer> xpaths;
    @JsonProperty("json_paths")
    private Map<String, TextContainer> jsonPaths;
    private Map<String, TextContainer> queries;
    private Map<String, TextContainer> cookies;
    private Map<String, TextContainer> forms;

    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("version", version)
                .add("URI", uri)
                .add("method", method)
                .add("headers", headers)
                .add("xpaths", xpaths)
                .add("json paths", jsonPaths)
                .add("queries", queries)
                .add("cookies", cookies)
                .add("forms", forms);
    }

    public RequestMatcher getRequestMatcher() {
        return factory.createRequestMatcher(this);
    }
}
