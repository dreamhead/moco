package com.github.dreamhead.moco.internal;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

public class MocoHttpServer {
    private final MocoServer server = new MocoServer();
    private final ActualHttpServer serverSetting;

    public MocoHttpServer(ActualHttpServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    public void start() {
        int port = server.start(serverSetting.getPort().or(0), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("handler", new MocoHandler(serverSetting));
            }
        });
        serverSetting.setPort(port);
    }

    public void stop() {
        server.stop();
    }
}
