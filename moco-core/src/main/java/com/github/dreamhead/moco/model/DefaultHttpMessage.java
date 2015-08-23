package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpMessage;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.google.common.collect.ImmutableMap;

public abstract class DefaultHttpMessage implements HttpMessage {
    private final HttpProtocolVersion version;
    private final MessageContent content;
    private final ImmutableMap<String, String> headers;

    protected DefaultHttpMessage(final HttpProtocolVersion version, final MessageContent content,
                              final ImmutableMap<String, String> headers) {
        this.version = version;
        this.content = content;
        this.headers = headers;
    }

    @Override
    public HttpProtocolVersion getVersion() {
        return this.version;
    }

    @Override
    public ImmutableMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public MessageContent getContent() {
        return this.content;
    }
}
