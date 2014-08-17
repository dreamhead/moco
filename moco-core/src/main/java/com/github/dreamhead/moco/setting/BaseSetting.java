package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.BaseResponseSettingConfiguration;
import com.github.dreamhead.moco.internal.SessionContext;

public abstract class BaseSetting<T extends ResponseSetting<T>> extends BaseResponseSettingConfiguration<T> {
    protected final RequestMatcher matcher;

    public BaseSetting(final RequestMatcher matcher) {
        this.matcher = matcher;
    }

    public boolean match(Request request) {
        return this.matcher.match(request) && this.handler != null;
    }

    public void writeToResponse(SessionContext context) {
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
