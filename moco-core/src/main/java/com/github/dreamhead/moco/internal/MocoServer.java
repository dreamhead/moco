package com.github.dreamhead.moco.internal;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MocoServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;

    public MocoServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    public int start(final int port, ChannelHandler pipelineFactory) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(pipelineFactory);

        try {
            future = bootstrap.bind(port).sync();
            SocketAddress socketAddress = future.channel().localAddress();
            InetSocketAddress address = (InetSocketAddress)socketAddress;
            return address.getPort();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        doStopGracefully();
    }
	
	public void stop(boolean shutDownGracefully) {
		if (shutDownGracefully) {
			doStopGracefully();
		} else {
			doStopImmediately();
		}
	}
	private void doStopGracefully() {
		closeChannel();

		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}


		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
		}
	}

	private void doStopImmediately() {
		closeChannel();

		if (bossGroup != null) {
	        terminateSilently(bossGroup);
	        bossGroup = null;
        }

        if (workerGroup != null) {
	        terminateSilently(workerGroup);
            workerGroup = null;
        }	    	    
    }

	private void closeChannel() {
		if (future != null) {
			future.channel().flush().closeFuture();
			future = null;
		}
	}

	private void terminateSilently(EventLoopGroup eventLoopGroup){
		eventLoopGroup.shutdownGracefully().syncUninterruptibly();	
	}
}
