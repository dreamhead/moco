package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.SocketRequest;
import com.google.common.base.MoreObjects;

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

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
                .add("content", content)
                .add("clientAddress", clientAddress)
                .toString();
    }
}
