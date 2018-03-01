package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.SocketRequest;

public final class DefaultSocketRequest implements SocketRequest {
    private final MessageContent content;

    public DefaultSocketRequest(final MessageContent content) {
        this.content = content;
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }
}
