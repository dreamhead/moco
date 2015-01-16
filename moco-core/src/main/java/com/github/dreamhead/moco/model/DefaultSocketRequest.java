package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.SocketRequest;

public class DefaultSocketRequest implements SocketRequest {
    private MessageContent content;

    public DefaultSocketRequest(MessageContent content) {
        this.content = content;
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }
}
