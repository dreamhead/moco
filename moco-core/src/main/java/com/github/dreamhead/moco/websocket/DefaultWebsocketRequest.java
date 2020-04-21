package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.model.MessageContent;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import static com.github.dreamhead.moco.util.ByteBufs.toByteArray;

public class DefaultWebsocketRequest implements WebsocketRequest {
    private MessageContent content;

    public DefaultWebsocketRequest(final WebSocketFrame frame) {
        byte[] bytes = toByteArray(frame.content());
        this.content = MessageContent.content().withContent(bytes).build();
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }
}
