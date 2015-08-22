package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpsCertificate;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class MocoHttpServer extends BaseServerRunner {
    private final ActualHttpServer serverSetting;

    public MocoHttpServer(final ActualHttpServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    @Override
    protected BaseActualServer serverSetting() {
        return this.serverSetting;
    }

    @Override
    protected ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                if (serverSetting.isSecure()) {
                    pipeline.addFirst("ssl", sslHandler().get());
                }

                pipeline.addLast("codec", new HttpServerCodec(4096, 8192, 8192, false));
                pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                pipeline.addLast("handler", new MocoHandler(serverSetting));
            }
        };
    }

    private Optional<SslHandler> sslHandler() {
        return serverSetting.getCertificate().transform(toSslHandler());
    }

    private Function<HttpsCertificate, SslHandler> toSslHandler() {
        return new Function<HttpsCertificate, SslHandler>() {
            @Override
            public SslHandler apply(final HttpsCertificate certificate) {
                SSLEngine sslEngine = certificate.createSSLEngine();
                sslEngine.setUseClientMode(false);
                return new SslHandler(sslEngine);
            }
        };
    }
}
