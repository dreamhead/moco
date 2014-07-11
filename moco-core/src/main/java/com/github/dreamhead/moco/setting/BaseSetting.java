package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;
import static com.google.common.collect.ImmutableList.of;

public class BaseSetting extends Setting implements ConfigApplier<BaseSetting> {
    public BaseSetting(RequestMatcher matcher) {
        super(matcher);
    }

    public boolean match(Request request) {
        return this.matcher.match(request) && this.handler != null;
    }

    public void writeToResponse(SessionContext context) {
        this.handler.writeToResponse(context);
        this.fireCompleteEvent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseSetting apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(this.matcher, config);
        if (config.isFor("uri") && this.matcher == appliedMatcher) {
            appliedMatcher = new AndRequestMatcher(of(appliedMatcher, context((String)config.apply(""))));
        }

        BaseSetting setting = new BaseSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }

    public void fireCompleteEvent() {
        for (MocoEventTrigger eventTrigger : eventTriggers) {
            if (eventTrigger.isFor(MocoEvent.COMPLETE)) {
                eventTrigger.fireEvent();
            }
        }
    }
}
