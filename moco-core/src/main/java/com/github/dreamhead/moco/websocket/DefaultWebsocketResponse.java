package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.ToStringHelper;

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
        return ToStringHelper.of(this)
                .add("content", content)
                .toString();
    }
}
