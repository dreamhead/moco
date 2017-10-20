package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.BootstrapTask;
import com.github.dreamhead.moco.bootstrap.ShutdownArgs;
import com.github.dreamhead.moco.internal.MocoClient;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;

public final class ShutdownTask implements BootstrapTask {
    private static Logger logger = LoggerFactory.getLogger(ShutdownTask.class);

    private final MocoClient client = new MocoClient();
    private final String shutdownKey;

    public ShutdownTask(final String shutdownKey) {
        this.shutdownKey = shutdownKey;
    }

    @Override
    public void run(final String[] args) {
        ShutdownArgs shutdownArgs = parse(args);
        client.run("127.0.0.1", shutdownArgs.getShutdownPort().get(), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("handler", new ShutdownHandler());
            }
        });
    }

    private class ShutdownHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(final ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(shutdownKey + "\r\n").addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            ctx.close();

            if (ConnectException.class.isInstance(cause)) {
                logger.error("fail to shutdown, please specify correct shutdown port.");
                return;
            }

            throw new RuntimeException(cause);
        }
    }
}
