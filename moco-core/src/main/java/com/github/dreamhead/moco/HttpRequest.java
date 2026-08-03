package com.github.dreamhead.moco;

import tools.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.model.DefaultHttpRequest;

import java.util.Map;

@JsonDeserialize(as = DefaultHttpRequest.class)
public interface HttpRequest extends Request, HttpMessage {
    String getUri();

    HttpMethod getMethod();

    Map<String, String[]> getQueries();
}
