package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.internal.MocoClient;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;

public class ShutdownTask implements BootstrapTask {
    private static Logger logger = LoggerFactory.getLogger(ShutdownTask.class);

    private final MocoClient client = new MocoClient();
    private final int defaultShutdownPort;
    private final String defaultShutdownKey;

    public ShutdownTask(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    @Override
    public void run(String[] args) {
        ShutdownArgs shutdownArgs = parse(args);
        client.run(shutdownArgs.getShutdownPort(defaultShutdownPort), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("handler", new ShutdownHandler());
            }
        });
    }

    private class ShutdownHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(defaultShutdownKey + "\r\n").addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();

            if (ConnectException.class.isInstance(cause)) {
                logger.error("fail to shutdown, please specify correct shutdown port.");
                return;
            }

            throw new RuntimeException(cause);
        }
    }
}
