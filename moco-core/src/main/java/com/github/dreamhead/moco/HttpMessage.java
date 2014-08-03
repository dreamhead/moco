package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpMessage {
    HttpProtocolVersion getVersion();

    ImmutableMap<String, String> getHeaders();
}
