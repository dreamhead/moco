package com.github.dreamhead.moco.internal;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MocoServer {
    private ServerBootstrap bootstrap;
    private ChannelGroup allChannels;

    public void start(final int port, ChannelPipelineFactory pipelineFactory) {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        allChannels = new DefaultChannelGroup();
        allChannels.add(bootstrap.bind(new InetSocketAddress(port)));
    }

    public void stop() {
        if (allChannels != null) {
            doStop();
        }
    }

    private void doStop() {
        allChannels.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
        allChannels = null;
        bootstrap = null;
    }
}
