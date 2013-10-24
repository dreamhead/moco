package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;

public abstract class ResponseSetting {
    protected ResponseHandler handler;
    protected List<MocoEventTrigger> eventTriggers = newArrayList();

    public ResponseSetting response(String content) {
        return this.response(text(content));
    }

    public ResponseSetting response(Resource resource) {
        return this.response(with(resource));
    }

    public ResponseSetting response(ResponseHandler handler) {
        if (handler == null) {
            return this;
        }

        if (this.handler != null) {
            throw new RuntimeException("handler has already been set");
        }

        this.handler = handler;
        return this;
    }

    public void response(MocoProcedure procedure) {
        this.response(with(procedure));
    }

    public void response(ResponseHandler... handlers) {
        this.response(new AndResponseHandler(copyOf(handlers)));
    }

    public void redirectTo(String url) {
        this.response(status(HttpResponseStatus.FOUND.code()), header("Location", url));
    }

    public ResponseSetting on(MocoEventTrigger trigger) {
        this.eventTriggers.add(trigger);
        return this;
    }

    protected static RequestMatcher context(String context) {
        return match(uri(context + ".*"));
    }
}
