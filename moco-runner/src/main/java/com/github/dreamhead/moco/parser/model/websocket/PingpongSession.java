package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.WebSocketServer;
import com.github.dreamhead.moco.parser.model.TextContainer;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PingpongSession {
    private TextContainer ping;
    private TextContainer pong;

    public final void bindPingPong(final WebSocketServer webSocketServer) {
        webSocketServer.ping(ping.asResource()).pong(pong.asResource());
    }
}

