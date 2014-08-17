package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.Runner;
import io.netty.channel.ChannelHandler;

public abstract class BaseServerRunner<T extends ResponseSetting<T>> extends Runner {
    protected abstract BaseActualServer<T> serverSetting();
    protected abstract ChannelHandler channelInitializer();

    protected final MocoServer server = new MocoServer();

    @Override
    public void start() {
        BaseActualServer<T> responseSetting = serverSetting();
        int port = server.start(responseSetting.getPort().or(0), channelInitializer());
        responseSetting.setPort(port);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
