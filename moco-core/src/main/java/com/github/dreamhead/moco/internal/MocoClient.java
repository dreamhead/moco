package com.github.dreamhead.moco.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MocoClient {

    private EventLoopGroup group;

    public MocoClient() {
        this.group = new NioEventLoopGroup();
    }

    public void run(final int port, final ChannelHandler pipelineFactory) {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(pipelineFactory);

        try {
            ChannelFuture f = bootstrap.connect("127.0.0.1", port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            group.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (group != null) {
            group.shutdownGracefully();
            group = null;
        }
    }
}
