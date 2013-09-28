package com.github.dreamhead.moco;

import java.util.Map;

public interface HttpRequest {
    Map<String, String> getQueries();

    String getMethod();

    String getVersion();

    String getContent();

    Map<String, String> getHeaders();
}
