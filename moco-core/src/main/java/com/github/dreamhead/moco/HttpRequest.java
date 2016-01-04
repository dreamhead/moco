package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpRequest extends Request, HttpMessage {
    String getUri();

    HttpMethod getMethod();

    ImmutableMap<String, String[]> getQueries();
}
