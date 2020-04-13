package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class DefaultWebsocketRequest implements WebsocketRequest {
    private MessageContent content;

    public DefaultWebsocketRequest(final WebSocketFrame frame) {
        ByteBuf buf = frame.content();
        byte[] bytes = new byte[buf.readableBytes()];
        int readerIndex = buf.readerIndex();
        buf.getBytes(readerIndex, bytes);
        this.content = MessageContent.content().withContent(bytes).build();
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }
}
