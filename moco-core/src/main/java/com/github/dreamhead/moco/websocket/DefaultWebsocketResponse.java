package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.MoreObjects;

public class DefaultWebsocketResponse implements MutableWebsocketResponse {
    private MessageContent content;

    @Override
    public final MessageContent getContent() {
        return this.content;
    }

    @Override
    public final void setContent(final MessageContent content) {
        this.content = content;
    }

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
                .add("content", content)
                .toString();
    }
}
