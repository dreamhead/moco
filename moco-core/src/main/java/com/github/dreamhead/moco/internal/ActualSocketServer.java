package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.SocketResponseSetting;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.setting.Setting;
import com.github.dreamhead.moco.setting.SocketSetting;

import static com.google.common.base.Optional.of;

public class ActualSocketServer extends BaseActualServer<SocketResponseSetting> implements SocketServer {
    public ActualSocketServer(int port) {
        super(of(port), null, new MocoConfig[0]);
    }

    @Override
    protected Setting<SocketResponseSetting> newSetting(RequestMatcher matcher) {
        return new SocketSetting(matcher);
    }

    @Override
    protected SocketResponseSetting onRequestAttached(RequestMatcher matcher) {
        SocketSetting baseSetting = new SocketSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }

    @Override
    protected SocketResponseSetting self() {
        return this;
    }
}
