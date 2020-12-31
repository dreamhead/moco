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

    public TextContainer getConnected() {
        return connected;
    }

    public boolean hasSessions() {
        return this.sessions != null && !this.sessions.isEmpty();
    }

    public void bindSessions(final WebSocketServer webSocketServer) {

        for (WebsocketSession session : sessions) {
            webSocketServer.request(by(session.request.asResource()))
                    .response(session.response.asResource());
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WebsocketSession {
        private TextContainer request;
        private TextContainer response;
    }
}
