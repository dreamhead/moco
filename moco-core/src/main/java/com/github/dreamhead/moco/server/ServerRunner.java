package com.github.dreamhead.moco.server;

import com.github.dreamhead.moco.Runner;

public final class ServerRunner extends Runner {
    private final ServerConfiguration configuration;

    private final MocoServer server = new MocoServer();

    public ServerRunner(final ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void start() {
        ServerSetting setting = configuration.serverSetting();
        int port = this.server.start(setting.getPort().orElse(0), configuration.channelInitializer());
        setting.setPort(port);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
