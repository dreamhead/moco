package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.MocoGroup;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.websocket.WebSocketBroadcastHandler;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static java.util.Objects.requireNonNull;

public final class MocoWebSockets {
    public static ResponseHandler broadcast(final String content) {
        return broadcast(text(checkNotNullOrEmpty(content, "Broadcast content should not be null")));
    }

    public static ResponseHandler broadcast(final String content, final MocoGroup group) {
        return broadcast(text(checkNotNullOrEmpty(content, "Broadcast content should not be null")),
                requireNonNull(group, "Group should not be null"));
    }

    public static ResponseHandler broadcast(final Resource content, final MocoGroup group) {
        return new WebSocketBroadcastHandler(requireNonNull(content, "Broadcast content should not be null"),
                requireNonNull(group, "Group should not be null"));
    }

    public static ResponseHandler broadcast(final Resource content) {
        return new WebSocketBroadcastHandler(requireNonNull(content, "Broadcast content should not be null"), null);
    }

    private MocoWebSockets() {
    }
}
