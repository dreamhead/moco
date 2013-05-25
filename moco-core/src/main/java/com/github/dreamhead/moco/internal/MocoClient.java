package com.github.dreamhead.moco.internal;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MocoClient {
    private ClientBootstrap bootstrap;

    public void run(final int port, final ChannelPipelineFactory pipelineFactory) {
        ChannelFactory factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(pipelineFactory);

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        bootstrap.connect(new InetSocketAddress("127.0.0.1", port));
    }

    public void stop() {
        if (bootstrap != null) {
            bootstrap.releaseExternalResources();
            bootstrap = null;
        }
    }
}
