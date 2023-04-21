package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.SocketRequest;
import com.github.dreamhead.moco.internal.Client;
import com.google.common.base.MoreObjects;

public final class DefaultSocketRequest implements SocketRequest {
    private final MessageContent content;
    private final Client client;

    public DefaultSocketRequest(final MessageContent content, final Client client) {
        this.content = content;
        this.client = client;
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
                .add("content", content)
                .add("client", client)
                .toString();
    }
}
