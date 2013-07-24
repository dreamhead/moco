package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.Lists.newArrayList;

public abstract class ResponseSetting {
    protected abstract void onResponseAttached(ResponseHandler handler);
    protected ResponseHandler handler;

    public void response(String content) {
        this.response(text(content));
    }

    public void response(Resource resource) {
        this.response(with(resource));
    }

    public void response(ResponseHandler handler) {
        if (this.handler != null) {
            throw new RuntimeException("handler has already been set");
        }

        this.handler = handler;

        this.onResponseAttached(this.handler);
    }

    public void response(ResponseHandler... handler) {
        this.response(new AndResponseHandler(newArrayList(handler)));
    }

    public void redirectTo(String url) {
        this.response(status(HttpResponseStatus.FOUND.code()), header("Location", url));
    }

    protected static RequestMatcher context(String context) {
        return match(uri(context + "\\w*"));
    }
}
