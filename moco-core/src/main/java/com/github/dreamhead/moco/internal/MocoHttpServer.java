package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.server.ServerConfiguration;
import com.github.dreamhead.moco.server.ServerSetting;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpDecoderConfig;
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
            protected void initChannel(final SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();

                if (serverSetting.isSecure()) {
                    pipeline.addFirst("ssl", serverSetting.getRequiredSslHandler());
                }

                ServerConfig serverConfig = serverSetting.getServerConfig();
                HttpDecoderConfig config = new HttpDecoderConfig().setMaxInitialLineLength(MAX_INITIAL_LINE_LENGTH)
                        .setMaxChunkSize(MAX_CHUNK_SIZE)
                        .setMaxHeaderSize(serverConfig.getHeaderSize())
                        .setValidateHeaders(false);
                pipeline.addLast("codec", new HttpServerCodec(config));
                pipeline.addLast("aggregator", new HttpObjectAggregator(serverConfig.getContentLength()));
                pipeline.addLast("handler", new MocoHandler(serverSetting));
            }
        };
    }
}
