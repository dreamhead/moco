package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpServer;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class MocoHttpServer {
    private ServerBootstrap bootstrap;
    private ChannelGroup allChannels;
    private final MocoHandler handler;
    private HttpServer server;

    public MocoHttpServer(HttpServer server) {
        this.server = server;
        this.handler = new MocoHandler(server.getSettings(), server.getAnyResponseHandler());
    }

    public void start() {
        doStart(server.getPort());
    }

    private void doStart(int port) {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("handler", handler);
                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(port));

        allChannels = new DefaultChannelGroup();
        try {
            allChannels.add(bootstrap.bind(new InetSocketAddress(InetAddress.getLocalHost(), port)));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (allChannels != null) {
            doStop();
        }
    }

    private void doStop() {
        allChannels.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
        allChannels = null;
        bootstrap = null;
    }
}
