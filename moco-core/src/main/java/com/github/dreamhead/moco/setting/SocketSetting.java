package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.SocketResponseSetting;

public class SocketSetting extends BaseSetting<SocketResponseSetting> implements Setting<SocketResponseSetting>, SocketResponseSetting {
    public SocketSetting(RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    protected SocketResponseSetting self() {
        return this;
    }

    @Override
    public Setting<SocketResponseSetting> apply(MocoConfig config) {
        return null;
    }
}
