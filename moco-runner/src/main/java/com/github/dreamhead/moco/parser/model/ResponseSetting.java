package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.parser.ResponseHandlerFactory;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.util.Jsons.toJson;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableSet.of;
import static java.lang.String.format;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseSetting extends Dynamics {
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
        return Objects.toStringHelper(this)
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
