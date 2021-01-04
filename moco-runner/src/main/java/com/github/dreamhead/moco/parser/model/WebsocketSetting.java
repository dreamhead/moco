package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.WebSocketServer;

import java.util.List;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.util.Iterables.isNullOrEmpty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketSetting {
    private String uri;
    private TextContainer connected;
    private List<PingpongSession> pingpongs;
    private List<WebsocketSession> sessions;

    public String getUri() {
        return this.uri;
    }

    public boolean hasSessions() {
        return !isNullOrEmpty(this.sessions);
    }

    private void bindSessions(final WebSocketServer webSocketServer) {
        for (WebsocketSession session : sessions) {
            webSocketServer.request(by(session.request.asResource()))
                    .response(session.response.asResource());
        }
    }

    public void bind(final WebSocketServer webSocketServer) {
        if (connected != null) {
            webSocketServer.connected(connected.asResource());
        }

        if (hasPingPongs()) {
            for (PingpongSession pingpong : pingpongs) {
                webSocketServer.ping(pingpong.ping.asResource()).pong(pingpong.pong.asResource());
            }
        }

        if (hasSessions()) {
            bindSessions(webSocketServer);
        }
    }

    private boolean hasPingPongs() {
        return !isNullOrEmpty(this.pingpongs);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WebsocketSession {
        private TextContainer request;
        private TextContainer response;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class PingpongSession {
        private TextContainer ping;
        private TextContainer pong;
    }
}
