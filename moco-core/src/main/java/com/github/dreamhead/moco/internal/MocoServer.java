package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.util.MocoExecutors;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public final class MocoServer {
    private EventLoopGroup group;
    private ChannelFuture future;

    public MocoServer() {
        group = new NioEventLoopGroup(0, MocoExecutors.executor());
    }

    public int start(final int port, final ChannelInitializer<? extends Channel> pipelineFactory) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(pipelineFactory);

        try {
            future = bootstrap.bind(port).sync();
            SocketAddress socketAddress = future.channel().localAddress();
            return ((InetSocketAddress) socketAddress).getPort();
        } catch (InterruptedException e) {
            throw new MocoException(e);
        }
    }

    public void stop() {
        doStop();
    }

    private void doStop() {
        if (future != null) {
            future.channel().close().syncUninterruptibly();
            future = null;
        }

        if (group != null) {
            Future<?> groupFuture = group.shutdownGracefully(0, 0, TimeUnit.SECONDS);
            try {
                groupFuture.get();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            group = null;
        }
    }
}
