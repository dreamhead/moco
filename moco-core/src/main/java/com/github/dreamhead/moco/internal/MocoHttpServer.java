package com.github.dreamhead.moco.internal;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class MocoHttpServer {
    private final MocoServer server = new MocoServer();
    private final ActualHttpServer serverSetting;

    public MocoHttpServer(ActualHttpServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    public void start() {
        server.start(serverSetting.getPort(), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("handler", new MocoHandler(serverSetting));
            }
        });
    }

    public void stop() {
        server.stop();
    }
}
