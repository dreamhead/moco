package com.github.dreamhead.moco.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public interface ServerConfiguration {
    ServerSetting serverSetting();
    ChannelInitializer<? extends Channel> channelInitializer();
}
