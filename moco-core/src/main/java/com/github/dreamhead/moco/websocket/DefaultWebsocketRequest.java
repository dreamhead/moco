package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.model.MessageContent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class DefaultWebsocketRequest implements WebsocketRequest {
    private TextWebSocketFrame frame;

    public DefaultWebsocketRequest(final TextWebSocketFrame frame) {
        this.frame = frame;
    }

    @Override
    public MessageContent getContent() {
        return MessageContent.content().withContent(this.frame.text()).build(); 
    }
}
