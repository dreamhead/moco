package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.MutableSocketResponse;

public final class DefaultSocketResponse implements MutableSocketResponse {
    private MessageContent content;

    @Override
    public MessageContent getContent() {
        return content;
    }

    @Override
    public void setContent(final MessageContent content) {
        this.content = content;
    }
}
