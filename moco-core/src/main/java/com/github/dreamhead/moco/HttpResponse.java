package com.github.dreamhead.moco;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public interface HttpResponse {
    String getVersion();

    String getContent();

    Map<String, String> getHeaders();

    int getStatusCode();
}
