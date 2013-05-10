package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.internal.MocoServer;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

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
        server.start(this.shutdownPort, new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("handler", new ShutdownHandler());
                return pipeline;
            }
        });
    }

    public void stopMonitor() {
        server.stop();
    }

    private class ShutdownHandler extends SimpleChannelHandler {
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            ChannelBuffer message = (ChannelBuffer)e.getMessage();

            if (shouldShutdown(message)) {
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

        private boolean shouldShutdown(ChannelBuffer message) {
            try {
                final String content = message.toString(Charset.defaultCharset());
                return shutdownKey.equals(CharStreams.readFirstLine(toSuppiler(content)));
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
