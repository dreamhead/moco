package com.github.moco;

import com.github.moco.handler.ContentHandler;

public abstract class Setting {
    protected final RequestMatcher matcher;
    protected ResponseHandler handler;

    protected Setting(RequestMatcher matcher) {
        this.matcher = matcher;
    }

    public void response(String content) {
        this.response(new ContentHandler(content));
    }

    public void response(ResponseHandler handler) {
        this.handler = handler;
    }
}
