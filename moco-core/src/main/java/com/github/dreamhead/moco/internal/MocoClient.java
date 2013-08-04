package com.github.dreamhead.moco.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MocoClient {
    public void run(final int port, final ChannelHandler pipelineFactory) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress("127.0.0.1", port)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(pipelineFactory);

        try {
            Channel channel = bootstrap.connect().sync().channel();
            ChannelFuture future = channel.closeFuture().sync();
            future.addListener(ChannelFutureListener.CLOSE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
