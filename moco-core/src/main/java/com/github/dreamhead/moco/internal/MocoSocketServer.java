package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.SocketResponseSetting;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class MocoSocketServer extends BaseServerRunner<SocketResponseSetting> {
    private final ActualSocketServer serverSetting;

    public MocoSocketServer(ActualSocketServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    @Override
    protected BaseActualServer<SocketResponseSetting> serverSetting() {
        return this.serverSetting;
    }

    @Override
    protected ChannelHandler channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("handler", new MocoSocketHandler(serverSetting));
            }
        };
    }
}
