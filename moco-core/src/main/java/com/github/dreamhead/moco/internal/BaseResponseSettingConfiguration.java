package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.ResponseSetting;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
import static java.util.Objects.requireNonNull;

public abstract class BaseResponseSettingConfiguration<T extends ResponseSetting<T>>
        extends AbstractResponseBase<T> implements ResponseSetting<T> {

    protected ResponseHandler handler;
    protected List<MocoEventTrigger> eventTriggers = new ArrayList<>();
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    protected BaseResponseSettingConfiguration() {
        this.clazz = (Class<T>) getClass();
    }

    private T self() {
        return clazz.cast(this);
    }

    @Override
    public final T response(final ResponseHandler handler, final ResponseHandler... handlers) {
        ResponseHandler responseHandler = and(requireNonNull(handler, "Handler should not be null"),
                requireNonNull(handlers, "Handlers should not be null"));
        this.handler = targetHandler(responseHandler);
        return self();
    }

    private ResponseHandler targetHandler(final ResponseHandler responseHandler) {
        if (this.handler == null) {
            return responseHandler;
        }

        return and(this.handler, responseHandler);
    }

    @Override
    public final T on(final MocoEventTrigger trigger) {
        this.eventTriggers.add(requireNonNull(trigger, "Trigger should not be null"));
        return self();
    }
}
