package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.model.ContentStream;

import static com.github.dreamhead.moco.Moco.text;

public abstract class ResponseSetting {
    protected ResponseHandler handler;

    public void response(String content) {
        this.response(text(content));
    }

    public void response(ContentStream stream) {
        this.response(new ContentHandler(stream));
    }

    public void response(ResponseHandler handler) {
        this.handler = handler;
    }

    public void response(ResponseHandler... handler) {
        this.response(new AndResponseHandler(handler));
    }
}
