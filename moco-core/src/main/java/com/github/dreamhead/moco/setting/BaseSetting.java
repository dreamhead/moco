package com.github.dreamhead.moco.setting;


import com.github.dreamhead.moco.MocoEvent;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.internal.BaseResponseSettingConfiguration;
import com.github.dreamhead.moco.internal.SessionContext;

public abstract class BaseSetting<T extends ResponseSetting<T>>
        extends BaseResponseSettingConfiguration<T> implements Setting<T> {
    protected final RequestMatcher matcher;

    public BaseSetting(final RequestMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(final Request request) {
        return this.matcher.match(request) && this.handler != null;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        this.handler.writeToResponse(context);
        this.fireCompleteEvent();
    }

    public void fireCompleteEvent() {
        for (MocoEventTrigger eventTrigger : eventTriggers) {
            if (eventTrigger.isFor(MocoEvent.COMPLETE)) {
                eventTrigger.fireEvent();
            }
        }
    }
}
