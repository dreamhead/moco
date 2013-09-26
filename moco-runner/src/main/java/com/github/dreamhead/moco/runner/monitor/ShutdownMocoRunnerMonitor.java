package com.github.dreamhead.moco.runner.monitor;

import com.github.dreamhead.moco.internal.MocoServer;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ShutdownMocoRunnerMonitor implements MocoRunnerMonitor {
    private static Logger logger = LoggerFactory.getLogger(ShutdownMocoRunnerMonitor.class);
    private final MocoServer server = new MocoServer();
    private final Optional<Integer> shutdownPort;
    private final String shutdownKey;
    private final ShutdownListener shutdownListener;
    private int port;

    public ShutdownMocoRunnerMonitor(Optional<Integer> shutdownPort, String shutdownKey, ShutdownListener shutdownListener) {
        this.shutdownPort = shutdownPort;
        this.shutdownKey = shutdownKey;
        this.shutdownListener = shutdownListener;
    }

    public void startMonitor() {
        int port = server.start(this.shutdownPort.or(0), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("handler", new ShutdownHandler());
            }
        });

        this.port = port;

        logger.info("Shutdown port is {}", port);
    }

    public void stopMonitor() {
        server.stop();
    }

    public int port() {
        return port;
    }

    private class ShutdownHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            if (shouldShutdown(msg)) {
                shutdownListener.onShutdown();
                shutdownMonitorSelf();
            }
        }

        private void shutdownMonitorSelf() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    stopMonitor();
                }
            }).start();
        }

        private boolean shouldShutdown(String message) {
            try {
                return shutdownKey.equals(CharStreams.readFirstLine(toSuppiler(message)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private InputSupplier<Reader> toSuppiler(final String content) {
            return new InputSupplier<Reader>() {
                @Override
                public Reader getInput() throws IOException {
                    return new StringReader(content);
                }
            };
        }
    }
}
