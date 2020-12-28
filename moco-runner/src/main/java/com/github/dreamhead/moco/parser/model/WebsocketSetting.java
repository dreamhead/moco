package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketSetting {
    private String uri;
    private TextContainer connected;

    public String getUri() {
        return this.uri;
    }

    public TextContainer getConnected() {
        return connected;
    }
}
