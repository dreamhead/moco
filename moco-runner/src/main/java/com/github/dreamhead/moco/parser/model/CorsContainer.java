package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CorsContainer {
    private String allowOrigin;

    public String getAllowOrigin() {
        return this.allowOrigin;
    }
}
