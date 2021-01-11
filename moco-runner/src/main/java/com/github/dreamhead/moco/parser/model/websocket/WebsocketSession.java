package com.github.dreamhead.moco.parser.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.WebSocketServer;
import com.github.dreamhead.moco.parser.model.TextContainer;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebsocketSession {
    private TextContainer request;
    private WebsocketResponseSetting response;

    public void bindSession(final WebSocketServer webSocketServer) {
        webSocketServer.request(Moco.by(request.asResource()))
                .response(response.asResponseHandler());
    }
}

