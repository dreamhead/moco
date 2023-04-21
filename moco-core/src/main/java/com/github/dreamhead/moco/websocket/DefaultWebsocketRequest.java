package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.internal.Client;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.MoreObjects;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import static com.github.dreamhead.moco.util.ByteBufs.toByteArray;

public class DefaultWebsocketRequest implements WebsocketRequest {
    private final MessageContent content;
    private final Client client;

    public DefaultWebsocketRequest(final WebSocketFrame frame, final Client client) {
        this.client = client;
        byte[] bytes = toByteArray(frame.content());
        this.content = MessageContent.content().withContent(bytes).build();
    }

    @Override
    public final MessageContent getContent() {
        return this.content;
    }

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
                .add("content", content)
                .add("client", client)
                .toString();
    }

    @Override
    public final Client getClient() {
        return this.client;
    }
}
