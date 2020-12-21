package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.internal.SessionGroup;
import com.github.dreamhead.moco.recorder.MocoGroup;
import io.netty.channel.Channel;

public class ContextSessionGroup implements SessionGroup {
    private final ChannelSessionGroup group;
    private final Channel channel;

    public ContextSessionGroup(final ChannelSessionGroup group, final Channel channel) {
        this.group = group;
        this.channel = channel;
    }

    @Override
    public void writeAndFlush(final Object message, final MocoGroup group) {
        this.group.writeAndFlush(message, group);
    }

    @Override
    public void join(final MocoGroup group) {
        this.group.join(group, channel);
    }
}
