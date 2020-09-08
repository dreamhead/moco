package com.github.dreamhead.moco;

import com.github.dreamhead.moco.websocket.WebSocketBroadcastHandler;

public class MocoWebSockets {
    public static ResponseHandler broadcast(final String content) {
        return new WebSocketBroadcastHandler(content);
    }
}
