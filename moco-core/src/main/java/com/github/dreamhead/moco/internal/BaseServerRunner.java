package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.Runner;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class BaseServerRunner<T extends ResponseSetting<T>> extends Runner {
    protected abstract BaseActualServer<T> serverSetting();
    protected abstract ChannelInitializer<? extends Channel> channelInitializer();

    protected final MocoServer server = new MocoServer();

    @Override
    public void start() {
        BaseActualServer<T> server = serverSetting();
        int port = this.server.start(server.getPort().or(0), channelInitializer());
        server.setPort(port);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
