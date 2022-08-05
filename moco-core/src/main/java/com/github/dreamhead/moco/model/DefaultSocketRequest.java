package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.SocketRequest;

public final class DefaultSocketRequest implements SocketRequest {
    private final MessageContent content;
    private final String clientAddress;

    public DefaultSocketRequest(final MessageContent content, final String clientAddress) {
        this.content = content;
        this.clientAddress = clientAddress;
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }

    @Override
    public String getClientAddress() {
        return clientAddress;
    }
}
