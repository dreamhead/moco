package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Runner;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class MocoHttpsServer extends Runner {

    private final MocoServer server = new MocoServer();
    private final ActualHttpServer serverSetting;

    public MocoHttpsServer(ActualHttpServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    @Override
    public void start() {
        int port = server.start(serverSetting.getPort().or(0), new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                SSLEngine sslEngine = MocoSslContextFactory.getServerContext().createSSLEngine();
                sslEngine.setUseClientMode(false);

                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("ssl", new SslHandler(sslEngine));
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("handler", new MocoHandler(serverSetting));
            }
        });
        serverSetting.setPort(port);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
