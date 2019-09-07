package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Runner;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class BaseServerRunner extends Runner {
    protected abstract ServerSetting serverSetting();
    protected abstract ChannelInitializer<? extends Channel> channelInitializer();

    private final MocoServer server = new MocoServer();

    @Override
    public final void start() {
        ServerSetting setting = serverSetting();
        int port = this.server.start(setting.getPort().orElse(0), channelInitializer());
        setting.setPort(port);
    }

    @Override
    public final void stop() {
        server.stop();
    }
}
