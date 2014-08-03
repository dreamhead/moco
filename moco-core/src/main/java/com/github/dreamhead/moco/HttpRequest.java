package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpRequest extends Request, HttpMessage {
    String getUri();

    String getMethod();

    ImmutableMap<String, String> getQueries();
}
