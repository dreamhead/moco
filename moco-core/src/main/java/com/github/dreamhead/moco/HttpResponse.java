package com.github.dreamhead.moco;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.ImmutableMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public interface HttpResponse {
    String getVersion();

    String getContent();

    ImmutableMap<String, String> getHeaders();

    int getStatusCode();
}
