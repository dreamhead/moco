package com.github.moco;

import com.github.moco.request.AnyRequestSetting;
import com.github.moco.request.BaseRequestSetting;
import com.github.moco.request.ContentRequestSetting;
import com.github.moco.request.UriRequestSetting;
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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MocoServer {
    private int port;
    private ServerBootstrap bootstrap;
    private ChannelGroup allChannels = new DefaultChannelGroup();
    private final MocoHandler handler;

    public MocoServer(int port) {
        this.port = port;
        this.handler = new MocoHandler();
    }

    public void start() {
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
        allChannels.add(bootstrap.bind(new java.net.InetSocketAddress("localhost", port)));
    }

    public void stop() {
        allChannels.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }


    public void addRequestSettings(BaseRequestSetting requestSetting) {
        this.handler.addRequestSetting(requestSetting);
    }

    public void response(String response) {
        new AnyRequestSetting(this).response(response);
    }

    public RequestSetting withContent(String requestContent) {
        return new ContentRequestSetting(this, requestContent);
    }

    public RequestSetting withUri(String uri) {
        return new UriRequestSetting(this, uri);
    }
}
