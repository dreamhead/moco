package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpResponse extends Response {
    HttpProtocolVersion getVersion();

    ImmutableMap<String, String> getHeaders();

    int getStatus();
}
