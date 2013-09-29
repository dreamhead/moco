package com.github.dreamhead.moco;

import java.util.Map;

public interface HttpRequest {
    String getUri();

    Map<String, String> getQueries();

    String getMethod();

    String getVersion();

    String getContent();

    Map<String, String> getHeaders();
}
