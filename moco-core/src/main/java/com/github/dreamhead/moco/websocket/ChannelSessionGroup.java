package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.internal.SessionGroup;
import io.netty.channel.group.ChannelGroup;

public class ChannelSessionGroup implements SessionGroup {
    private final ChannelGroup group;

    public ChannelSessionGroup(final ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void writeAndFlush(final Object message) {
        this.group.writeAndFlush(message);
    }
}
