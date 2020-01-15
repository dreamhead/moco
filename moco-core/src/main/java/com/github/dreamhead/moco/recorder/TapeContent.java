package com.github.dreamhead.moco.recorder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.HttpRequest;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TapeContent {
    private Map<String, HttpRequest> content;

    @JsonCreator
    public TapeContent(@JsonProperty("content") final Map<String, HttpRequest> content) {
        this.content = content;
    }

    public TapeContent() {
        this.content = new HashMap<>();
    }

    public final void addRequest(final String name, final HttpRequest request) {
        content.put(name, request);
    }

    public final HttpRequest getRequest(final String name) {
        return content.get(name);
    }
}
