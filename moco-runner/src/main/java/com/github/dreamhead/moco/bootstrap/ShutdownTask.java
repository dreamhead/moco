package com.github.dreamhead.moco.bootstrap;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;
import static org.jboss.netty.buffer.ChannelBuffers.buffer;

public class ShutdownTask implements BootstrapTask {
    private final int defaultShutdownPort;
    private final String defaultShutdownKey;
    private MocoClient client = new MocoClient();

    public ShutdownTask(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    @Override
    public void run(String[] args) {
        ShutdownArgs shutdownArgs = parse(args);
        client.run(shutdownArgs.getShutdownPort(defaultShutdownPort), new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new ShutdownHandler());
            }
        });
    }

    private static class MocoClient {
        private ClientBootstrap bootstrap;

        public void run(final int port, final ChannelPipelineFactory pipelineFactory) {
            ChannelFactory factory = new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

            bootstrap = new ClientBootstrap(factory);
            bootstrap.setPipelineFactory(pipelineFactory);

            bootstrap.setOption("tcpNoDelay", true);
            bootstrap.setOption("keepAlive", true);

            bootstrap.connect(new InetSocketAddress("127.0.0.1", port));
        }

        public void stop() {
            if (bootstrap != null) {
                bootstrap.releaseExternalResources();
                bootstrap = null;
            }
        }
    }

    private class ShutdownHandler extends SimpleChannelHandler {
        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            byte[] bytes = (defaultShutdownKey + "\r\n").getBytes();
            ChannelBuffer buf = buffer(bytes.length);
            buf.writeBytes(bytes);
            Channels.write(ctx, e.getFuture(), buf);
        }

        @Override
        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.stop();
                }
            }).start();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            Throwable cause = e.getCause();
            if (ConnectException.class.isInstance(cause)) {
                System.err.println("fail to shutdown, please specify correct shutdown port.");
                return;
            }

            throw new RuntimeException(cause);
        }
    }
}
