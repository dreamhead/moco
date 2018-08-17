package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MocoClient {
    public final void run(final String host, final int port, final ChannelHandler pipelineFactory) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(pipelineFactory);

        try {
            Channel channel = bootstrap.connect().sync().channel();
            ChannelFuture future = channel.closeFuture().sync();
            future.addListener(ChannelFutureListener.CLOSE);
        } catch (InterruptedException e) {
            throw new MocoException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
