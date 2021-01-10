package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoWebSockets;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.WebSocketServer;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.asFileResource;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.isNullOrEmpty;
import static com.github.dreamhead.moco.util.Iterables.tail;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketSetting {
    private String uri;
    private TextContainer connected;
    private List<PingpongSession> pingpongs;
    private List<WebsocketSession> sessions;

    public String getUri() {
        return this.uri;
    }

    private boolean hasSessions() {
        return !isNullOrEmpty(this.sessions);
    }

    private void bindSessions(final WebSocketServer webSocketServer) {
        for (WebsocketSession session : sessions) {
            webSocketServer.request(by(session.request.asResource()))
                    .response(session.response.asResponseHandler());
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


}
