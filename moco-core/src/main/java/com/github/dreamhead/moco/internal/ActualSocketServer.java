package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.dumper.SocketRequestDumper;
import com.github.dreamhead.moco.dumper.SocketResponseDumper;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.Setting;
import com.github.dreamhead.moco.setting.SocketSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.util.Configs.configItem;

public class ActualSocketServer extends BaseActualServer<SocketResponseSetting> implements SocketServer {
    private ActualSocketServer(final Optional<Integer> port, final MocoMonitor monitor) {
        super(port, monitor, new MocoConfig[0]);
    }

    @Override
    protected Setting<SocketResponseSetting> newSetting(final RequestMatcher matcher) {
        return new SocketSetting(matcher);
    }

    @Override
    protected SocketResponseSetting onRequestAttached(final RequestMatcher matcher) {
        SocketSetting baseSetting = new SocketSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }

    public SocketServer mergeHttpServer(final ActualSocketServer thatServer) {
        ActualSocketServer newServer = newBaseServer();
        newServer.addSettings(this.getSettings());
        newServer.addSettings(thatServer.getSettings());

        newServer.anySetting(configItem(this.matcher, this.configs), configItem(this.handler, this.configs));
        newServer.anySetting(configItem(thatServer.matcher, thatServer.configs), configItem(thatServer.handler, thatServer.configs));

        newServer.addEvents(this.eventTriggers);
        newServer.addEvents(thatServer.eventTriggers);

        return newServer;
    }

    private ActualSocketServer newBaseServer() {
        return createLogServer(this.getPort());
    }

    public static ActualSocketServer createQuietServer(final Optional<Integer> port) {
        return new ActualSocketServer(port, new QuietMonitor());
    }

    public static ActualSocketServer createServerWithMonitor(final Optional<Integer> port, final MocoMonitor monitor) {
        return new ActualSocketServer(port, monitor);
    }

    public static ActualSocketServer createLogServer(final Optional<Integer> port) {
        return new ActualSocketServer(port, new Slf4jMonitor(new SocketRequestDumper(), new SocketResponseDumper()));
    }
}
