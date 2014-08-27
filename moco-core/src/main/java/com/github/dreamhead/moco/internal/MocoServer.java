package com.github.dreamhead.moco.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.internal.Awaiter.awaitUntil;

public class MocoServer {
    private static final int DEFAULT_TIMEOUT = 3;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;
    private InetSocketAddress address;

    public MocoServer() {
        bossGroup = new NioEventLoopGroup(1);
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
            address = (InetSocketAddress) socketAddress;
            return address.getPort();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        doStop();
        if (address != null) {
            awaitUntil(serverIsClosed(address), DEFAULT_TIMEOUT);
            address = null;
        }
    }

    private void doStop() {
        if (future != null) {
            future.channel().close().syncUninterruptibly();
            future = null;
        }
        
        Future<?> bossGroupFuture = null;
		Future<?> workerGroupFuture = null;

        if (bossGroup != null) {
            bossGroupFuture = bossGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS);
            bossGroup = null;
        }

        if (workerGroup != null) {
            workerGroupFuture = workerGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS);
            workerGroup = null;
        }
        
        synchronizeShutdownEventGroups(bossGroupFuture, workerGroupFuture);
		
    }
    
    private void synchronizeShutdownEventGroups(Future<?> bossGroupFuture, Future<?> workerGroupFuture) {
        
        try {
	        
	        if(bossGroupFuture != null) {
			    bossGroupFuture.get();
        	}
        	
        	if(workerGroupFuture != null) {
			    workerGroupFuture.get();
        	}
        	
        } catch (InterruptedException e) {
		    throw new IllegalStateException(e);
	    } catch (ExecutionException e) {
		    throw new IllegalStateException(e);
	    }
    }

    private Callable<Boolean> serverIsClosed(final InetSocketAddress address) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    Socket socket = new Socket();
                    socket.connect(address);
                    return false;
                } catch (ConnectException e) {
                    return true;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
