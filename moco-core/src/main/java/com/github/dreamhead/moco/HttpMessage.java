package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpMessage extends Message {
    HttpProtocolVersion getVersion();

    ImmutableMap<String, String[]> getHeaders();

    String getHeader(String name);
}
