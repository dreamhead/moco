package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.MutableSocketResponse;

public class DefaultSocketResponse implements MutableSocketResponse {
    private String content;

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }
}
