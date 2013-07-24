package com.github.dreamhead.moco.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MocoServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public MocoServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    public void start(final int port, ChannelHandler pipelineFactory) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(pipelineFactory);

        bootstrap.bind(port);
    }

    public void stop() {
        doStop();
    }

    private void doStop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup = null;
        }


        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }
}
