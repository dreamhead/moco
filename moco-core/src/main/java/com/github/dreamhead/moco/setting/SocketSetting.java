package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.SocketResponseSetting;

import static com.github.dreamhead.moco.util.Configs.configItem;
import static com.github.dreamhead.moco.util.Configs.configItems;

public class SocketSetting extends BaseSetting<SocketResponseSetting>
        implements Setting<SocketResponseSetting>, SocketResponseSetting {
    public SocketSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected Setting<SocketResponseSetting> createSetting(final RequestMatcher appliedMatcher,
                                                           final MocoConfig config) {
        SocketSetting setting = new SocketSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }

    @Override
    protected RequestMatcher configMatcher(final RequestMatcher matcher, final MocoConfig config) {
        return configItem(matcher, config);
    }
}
