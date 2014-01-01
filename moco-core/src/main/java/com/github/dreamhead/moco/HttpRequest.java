package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpRequest {
    String getUri();

    String getMethod();

    String getVersion();

    String getContent();

    ImmutableMap<String, String> getHeaders();

    ImmutableMap<String, String> getQueries();
}
