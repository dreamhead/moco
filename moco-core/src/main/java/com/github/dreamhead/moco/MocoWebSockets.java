package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.websocket.WebSocketBroadcastHandler;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoWebSockets {
    public static ResponseHandler broadcast(final String content) {
        return broadcast(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static ResponseHandler broadcast(final Resource content) {
        return new WebSocketBroadcastHandler(checkNotNull(content, "Content should not be null"));
    }

    private MocoWebSockets() {
    }
}
