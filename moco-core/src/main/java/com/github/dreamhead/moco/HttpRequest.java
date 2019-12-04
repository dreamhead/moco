package com.github.dreamhead.moco;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.collect.ImmutableMap;

@JsonDeserialize(as = DefaultHttpRequest.class)
public interface HttpRequest extends Request, HttpMessage {
    String getUri();

    HttpMethod getMethod();

    ImmutableMap<String, String[]> getQueries();
}
