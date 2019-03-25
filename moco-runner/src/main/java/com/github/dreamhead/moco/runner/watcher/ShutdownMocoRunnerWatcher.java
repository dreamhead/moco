package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.internal.MocoServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.io.CharSource.wrap;
import static io.netty.channel.ChannelHandler.Sharable;

public final class ShutdownMocoRunnerWatcher implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(ShutdownMocoRunnerWatcher.class);
    private final MocoServer server = new MocoServer();
    private final int shutdownPort;
    private final String shutdownKey;
    private final ShutdownListener shutdownListener;
    private int port;

    public ShutdownMocoRunnerWatcher(final int shutdownPort,
                                     final String shutdownKey,
                                     final ShutdownListener shutdownListener) {
        this.shutdownPort = shutdownPort;
        this.shutdownKey = shutdownKey;
        this.shutdownListener = shutdownListener;
    }

    public void start() {
        int actualPort = server.start(this.shutdownPort, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("handler", new ShutdownHandler());
            }
        });

        this.port = actualPort;

        logger.info("Shutdown port is {}", actualPort);
    }

    public void stop() {
        server.stop();
    }

    public int port() {
        return port;
    }

    @Sharable
    private class ShutdownHandler extends SimpleChannelInboundHandler<String> {
        private final ExecutorService service = Executors.newCachedThreadPool();

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
            if (shouldShutdown(msg)) {
                shutdownListener.onShutdown();
                shutdownMonitorSelf();
            }
        }

        private void shutdownMonitorSelf() {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            });
        }

        private boolean shouldShutdown(final String message) {
            try {
                return shutdownKey.equals(wrap(message).readFirstLine());
            } catch (IOException e) {
                throw new MocoException(e);
            }
        }
    }
}
