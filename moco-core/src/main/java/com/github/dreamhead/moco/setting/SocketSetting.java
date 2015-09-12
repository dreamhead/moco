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
    public Setting<SocketResponseSetting> apply(final MocoConfig config) {
        RequestMatcher appliedMatcher = configItem(this.matcher, config);
        SocketSetting setting = new SocketSetting(appliedMatcher);
        setting.handler = configItem(this.handler, config);
        setting.eventTriggers = configItems(eventTriggers, config);
        return setting;
    }
}
