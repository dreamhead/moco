package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.Runner;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class BaseServerRunner<T extends ResponseSetting<T>, U extends BaseActualServer> extends Runner {
    protected abstract BaseActualServer<T, U> serverSetting();
    protected abstract ChannelInitializer<? extends Channel> channelInitializer();

    private final MocoServer server = new MocoServer();

    @Override
    public final void start() {
        BaseActualServer<T, U> setting = serverSetting();
        int port = this.server.start(setting.getPort().or(0), channelInitializer());
        setting.setPort(port);
    }

    @Override
    public final void stop() {
        server.stop();
    }
}
