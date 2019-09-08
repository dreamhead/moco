package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.server.ServerConfiguration;
import com.github.dreamhead.moco.server.ServerSetting;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class MocoHttpServer implements ServerConfiguration {
    private static final int MAX_INITIAL_LINE_LENGTH = 4096;
    private static final int MAX_CHUNK_SIZE = 8192;
    private final ActualHttpServer serverSetting;

    public MocoHttpServer(final ActualHttpServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    @Override
    public final ServerSetting serverSetting() {
        return this.serverSetting;
    }

    @Override
    public final ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                if (serverSetting.isSecure()) {
                    pipeline.addFirst("ssl", serverSetting.sslHandler().get());
                }

                ServerConfig serverConfig = serverSetting.getServerConfig();
                pipeline.addLast("codec", new HttpServerCodec(MAX_INITIAL_LINE_LENGTH,
                        serverConfig.getHeaderSize(),
                        MAX_CHUNK_SIZE, false));
                pipeline.addLast("aggregator", new HttpObjectAggregator(serverConfig.getContentLength()));
                pipeline.addLast("handler", new MocoHandler(serverSetting));
            }
        };
    }
}
