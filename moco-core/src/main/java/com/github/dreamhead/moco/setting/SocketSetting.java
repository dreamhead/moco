package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.SocketResponseSetting;

import static com.github.dreamhead.moco.util.Configs.configItem;

public class SocketSetting extends BaseSetting<SocketResponseSetting>
        implements Setting<SocketResponseSetting>, SocketResponseSetting {
    public SocketSetting(final RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected final BaseSetting<SocketResponseSetting> createSetting(final RequestMatcher matcher) {
        return new SocketSetting(matcher);
    }

    @Override
    protected final RequestMatcher configMatcher(final RequestMatcher matcher, final MocoConfig config) {
        return configItem(matcher, config);
    }
}
