package com.github.dreamhead.moco.setting;


import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEvent;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.internal.BaseResponseSettingConfiguration;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.collect.ImmutableList;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;

public abstract class BaseSetting<T extends ResponseSetting<T>>
        extends BaseResponseSettingConfiguration<T> implements Setting<T> {
    private final RequestMatcher matcher;

    protected abstract Setting<T> createSetting(final RequestMatcher matcher,
                                                final ResponseHandler responseHandler,
                                                final ImmutableList<MocoEventTrigger> eventTriggers);

    protected abstract RequestMatcher configMatcher(final RequestMatcher matcher, final MocoConfig config);

    protected BaseSetting(final RequestMatcher matcher) {
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
        return createSetting(configMatcher(this.matcher, config),
                configItem(this.handler, config), configItems(eventTriggers, config));
    }
}
