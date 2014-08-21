package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.setting.Setting;
import com.github.dreamhead.moco.setting.SocketSetting;

import static com.google.common.base.Optional.of;

public class ActualSocketServer extends BaseActualServer<SocketResponseSetting> implements SocketServer {
    private ActualSocketServer(int port, MocoMonitor monitor) {
        super(of(port), monitor, new MocoConfig[0]);
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

    public static ActualSocketServer createQuietServer(int port) {
        return new ActualSocketServer(port, new QuietMonitor());
    }

    public static ActualSocketServer createLogServer(int port) {
        return new ActualSocketServer(port, null);
    }
}
