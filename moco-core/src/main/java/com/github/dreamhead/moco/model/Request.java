package com.github.dreamhead.moco.model;

import java.util.Map;

public interface Request {
    Map<String, String> getQueries();

    String getMethod();

    String getVersion();

    String getContent();

    Map<String, String> getHeaders();
}
