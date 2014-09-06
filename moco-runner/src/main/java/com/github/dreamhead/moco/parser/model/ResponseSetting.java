package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.ResponseHandlerFactory;
import com.google.common.base.MoreObjects;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseSetting {
    private final ResponseHandlerFactory factory = new DynamicResponseHandlerFactory();

    private String status;
    private ProxyContainer proxy;
    private Map<String, TextContainer> headers;
    private Map<String, TextContainer> cookies;
    private Long latency;
    private TextContainer text;
    private TextContainer file;
    @JsonProperty("path_resource")
    private TextContainer pathResource;
    private TextContainer version;
    private Object json;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("file", file)
                .add("version", version)
                .add("status", status)
                .add("headers", headers)
                .add("cookies", cookies)
                .add("proxy", proxy)
                .add("latency", latency)
                .toString();
    }

    public ResponseHandler getResponseHandler() {
        return factory.createResponseHandler(this);
    }
}
