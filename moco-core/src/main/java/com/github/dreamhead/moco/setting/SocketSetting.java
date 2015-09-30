package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.SocketResponseSetting;
import com.google.common.collect.ImmutableList;

import static com.github.dreamhead.moco.util.Configs.configItem;

public class SocketSetting extends BaseSetting<SocketResponseSetting>
        implements Setting<SocketResponseSetting>, SocketResponseSetting {
    public SocketSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected Setting<SocketResponseSetting> createSetting(final RequestMatcher matcher,
                                                           final ResponseHandler responseHandler,
                                                           final ImmutableList<MocoEventTrigger> eventTriggers) {
        SocketSetting setting = new SocketSetting(matcher);
        setting.handler = responseHandler;
        setting.eventTriggers = eventTriggers;
        return setting;
    }

    @Override
    protected RequestMatcher configMatcher(final RequestMatcher matcher, final MocoConfig config) {
        return configItem(matcher, config);
    }
}
