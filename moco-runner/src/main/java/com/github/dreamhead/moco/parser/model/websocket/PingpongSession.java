package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.parser.model.TextContainer;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PingpongSession {
    public TextContainer ping;
    public TextContainer pong;
}

