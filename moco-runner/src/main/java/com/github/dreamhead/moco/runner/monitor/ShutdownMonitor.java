package com.github.dreamhead.moco.runner.monitor;

import com.github.dreamhead.moco.internal.MocoServer;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ShutdownMonitor implements Monitor {
    private final MocoServer server = new MocoServer();
    private final int shutdownPort;
    private final String shutdownKey;
    private final ShutdownListener shutdownListener;

    public ShutdownMonitor(int shutdownPort, String shutdownKey, ShutdownListener shutdownListener) {
        this.shutdownPort = shutdownPort;
        this.shutdownKey = shutdownKey;
        this.shutdownListener = shutdownListener;
    }

    public void startMonitor() {
        server.start(this.shutdownPort, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("handler", new ShutdownHandler());
            }
        });
    }

    public void stopMonitor() {
        server.stop();
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
