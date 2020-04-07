package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.model.MessageContent;

public class DefaultWebsocketResponse implements MutableWebsocketResponse {
    private MessageContent content;

    @Override
    public MessageContent getContent() {
        return this.content;
    }

    @Override
    public void setContent(final MessageContent content) {
        this.content = content;
    }
}
