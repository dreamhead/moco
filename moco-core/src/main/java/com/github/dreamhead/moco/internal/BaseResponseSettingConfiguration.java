package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.resource.Resource;

import java.util.List;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public abstract class BaseResponseSettingConfiguration<T extends ResponseSetting<T>> {
    protected abstract T self();

    protected ResponseHandler handler;
    protected List<MocoEventTrigger> eventTriggers = newArrayList();

    public T response(final String content) {
        return this.response(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public T response(final Resource resource) {
        return this.response(with(checkNotNull(resource, "Resource should not be null")));
    }

    public T response(final MocoProcedure procedure) {
        return this.response(with(checkNotNull(procedure, "Procedure should not be null")));
    }

    public T response(final ResponseHandler... handlers) {
        return this.response(and(handlers));
    }

    public T response(final ResponseHandler handler) {
        this.handler = targetHandler(checkNotNull(handler, "Handler should not be null"));
        return self();
    }

    private ResponseHandler targetHandler(ResponseHandler responseHandler) {
        if (this.handler == null) {
            return responseHandler;
        }

        return and(this.handler, responseHandler);
    }

    public T on(final MocoEventTrigger trigger) {
        this.eventTriggers.add(checkNotNull(trigger, "Trigger should not be null"));
        return self();
    }
}
