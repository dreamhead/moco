package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.WebSocketServer;

import java.util.List;

import static com.github.dreamhead.moco.Moco.by;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketSetting {
    private String uri;
    private TextContainer connected;
    private List<WebsocketSession> sessions;

    public String getUri() {
        return this.uri;
    }

    public boolean hasSessions() {
        return this.sessions != null && !this.sessions.isEmpty();
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

        if (hasSessions()) {
            bindSessions(webSocketServer);
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WebsocketSession {
        private TextContainer request;
        private TextContainer response;
    }
}
