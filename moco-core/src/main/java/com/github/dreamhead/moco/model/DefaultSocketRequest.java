package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.SocketRequest;

public class DefaultSocketRequest implements SocketRequest {
    private String content;

    public DefaultSocketRequest(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return this.content;
    }
}
