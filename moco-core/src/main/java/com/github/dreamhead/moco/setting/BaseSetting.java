package com.github.dreamhead.moco.setting;


import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEvent;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.internal.BaseResponseSettingConfiguration;
import com.github.dreamhead.moco.internal.SessionContext;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;

public abstract class BaseSetting<T extends ResponseSetting<T>>
        extends BaseResponseSettingConfiguration<T> implements Setting<T> {
    private final RequestMatcher matcher;

    protected abstract BaseSetting<T> createSetting(RequestMatcher matcher);

    protected abstract RequestMatcher configMatcher(RequestMatcher matcher, MocoConfig config);

    protected BaseSetting(final RequestMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public final boolean match(final Request request) {
        return this.matcher.match(request) && this.handler != null;
    }

    @Override
    public final void writeToResponse(final SessionContext context) {
        this.handler.writeToResponse(context);
        this.fireCompleteEvent(context.getRequest());
    }

    private void fireCompleteEvent(final Request request) {
        for (MocoEventTrigger eventTrigger : eventTriggers) {
            if (eventTrigger.isFor(MocoEvent.COMPLETE)) {
                eventTrigger.fireEvent(request);
            }
        }
    }

    @Override
    public final Setting<T> apply(final MocoConfig config) {
        BaseSetting<T> setting = createSetting(configMatcher(this.matcher, config));
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }
}
