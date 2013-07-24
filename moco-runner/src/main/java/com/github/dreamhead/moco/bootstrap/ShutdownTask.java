package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.internal.MocoClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.net.ConnectException;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;

public class ShutdownTask implements BootstrapTask {
    private final int defaultShutdownPort;
    private final String defaultShutdownKey;
    private final MocoClient client = new MocoClient();

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
                ch.pipeline().addLast(new ShutdownHandler());
            }
        });
    }

    private class ShutdownHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            byte[] bytes = (defaultShutdownKey + "\r\n").getBytes();
//            ByteBu buf = buffer(bytes.length);
//            buf.writeBytes(bytes);
//            Channels.write(ctx, e.getFuture(), buf);
            ctx.writeAndFlush(defaultShutdownKey + "\r\n");

        }

//        @Override
//        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//            byte[] bytes = (defaultShutdownKey + "\r\n").getBytes();
//            ChannelBuffer buf = buffer(bytes.length);
//            buf.writeBytes(bytes);
//            Channels.write(ctx, e.getFuture(), buf);
//        }


        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.stop();
                }
            }).start();
        }

//        @Override
//        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    client.stop();
//                }
//            }).start();
//        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (ConnectException.class.isInstance(cause)) {
                System.err.println("fail to shutdown, please specify correct shutdown port.");
                return;
            }

            throw new RuntimeException(cause);
        }
    }
}
