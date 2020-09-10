package com.github.dreamhead.moco;

import com.github.dreamhead.moco.websocket.WebSocketBroadcastHandler;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

public class MocoWebSockets {
    public static ResponseHandler broadcast(final String content) {
        return new WebSocketBroadcastHandler(checkNotNullOrEmpty(content, "Content should not be null"));
    }
}
