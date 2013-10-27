package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;

public abstract class ResponseSetting {
    protected ResponseHandler handler;
    protected List<MocoEventTrigger> eventTriggers = newArrayList();

    public ResponseSetting response(String content) {
        return this.response(text(checkNotNull(content, "Content should not be null")));
    }

    public ResponseSetting response(Resource resource) {
        return this.response(with(checkNotNull(resource, "Resource should not be null")));
    }

    public ResponseSetting response(ResponseHandler handler) {
        if (this.handler != null) {
            throw new RuntimeException("handler has already been set");
        }

        this.handler = checkNotNull(handler, "Handler should not be null");
        return this;
    }

    public ResponseSetting response(MocoProcedure procedure) {
        this.response(with(checkNotNull(procedure, "Procedure should not be null")));
        return this;
    }

    public ResponseSetting response(ResponseHandler... handlers) {
        this.response(new AndResponseHandler(copyOf(handlers)));
        return this;
    }

    public ResponseSetting redirectTo(String url) {
        this.response(status(HttpResponseStatus.FOUND.code()), header("Location", checkNotNull(url, "URL should not be null")));
        return this;
    }

    public ResponseSetting on(MocoEventTrigger trigger) {
        this.eventTriggers.add(checkNotNull(trigger, "Trigger should not be null"));
        return this;
    }

    protected static RequestMatcher context(String context) {
        return match(uri(context + ".*"));
    }
}
