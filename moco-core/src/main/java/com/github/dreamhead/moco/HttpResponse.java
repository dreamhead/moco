package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMap;

public interface HttpResponse {
    String getVersion();

    String getContent();

    ImmutableMap<String, String> getHeaders();

    int getStatus();
}
