package com.github.dreamhead.moco.setting;


import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEvent;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.internal.BaseResponseSettingConfiguration;
import com.github.dreamhead.moco.internal.SessionContext;

public abstract class BaseSetting<T extends ResponseSetting<T>>
        extends BaseResponseSettingConfiguration<T> implements Setting<T> {
    private final RequestMatcher matcher;

    protected abstract Setting<T> createSetting(final RequestMatcher appliedMatcher,
                                                                  final MocoConfig config);
    protected abstract RequestMatcher configMatcher(final RequestMatcher matcher,
                                           final MocoConfig config);

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

    @Override
    public Setting<T> apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configMatcher(this.matcher, config);
        return createSetting(appliedMatcher, config);
    }
}
