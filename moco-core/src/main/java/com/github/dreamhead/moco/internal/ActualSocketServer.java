package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.SocketResponseSetting;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.dumper.SocketRequestDumper;
import com.github.dreamhead.moco.dumper.SocketResponseDumper;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.monitor.Slf4jMonitor;
import com.github.dreamhead.moco.setting.Setting;
import com.github.dreamhead.moco.setting.SocketSetting;

public final class ActualSocketServer extends BaseActualServer<SocketResponseSetting, ActualSocketServer>
        implements SocketServer {
    private ActualSocketServer(final int port, final MocoMonitor monitor) {
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

    @Override
    protected ActualSocketServer createMergeServer(final ActualSocketServer thatServer) {
        return newBaseServer(this.getPort().or(thatServer.getPort()).or(0));
    }

    private ActualSocketServer newBaseServer(final int thisPort) {
        return createLogServer(thisPort);
    }

    public static ActualSocketServer createQuietServer(final int port) {
        return new ActualSocketServer(port, new QuietMonitor());
    }

    public static ActualSocketServer createServerWithMonitor(final int port, final MocoMonitor monitor) {
        return new ActualSocketServer(port, monitor);
    }

    public static ActualSocketServer createLogServer(final int port) {
        return new ActualSocketServer(port, new Slf4jMonitor(new SocketRequestDumper(), new SocketResponseDumper()));
    }
}
