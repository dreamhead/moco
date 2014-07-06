package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpRequest extends Request {
    String getUri();

    String getMethod();

    HttpProtocolVersion getVersion();

    ImmutableMap<String, String> getHeaders();

    ImmutableMap<String, String> getQueries();
}
