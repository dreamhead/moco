package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.model.ContentStream;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ResponseSetting {
    protected ResponseHandler handler;

    public void response(String content) {
        checkNotNull(content, "Null content is not allowed");
        this.response(new ContentHandler(content));
    }

    public void response(ContentStream stream) {
        this.response(new ContentHandler(stream.asByteArray()));
    }

    public void response(ResponseHandler handler) {
        this.handler = handler;
    }

    public void response(ResponseHandler... handler) {
        this.handler = new AndResponseHandler(handler);
    }
}
