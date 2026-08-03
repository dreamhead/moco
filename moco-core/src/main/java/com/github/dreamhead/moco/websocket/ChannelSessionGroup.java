package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.recorder.MocoGroup;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelSessionGroup {
    private final ChannelGroup group;
    private final Map<MocoGroup, Set<Channel>> groupChannels;
    private final Map<Channel, MocoGroup> channelGroups;

    public ChannelSessionGroup(final ChannelGroup group) {
        this.group = group;
        this.groupChannels = new ConcurrentHashMap<>();
        this.channelGroups = new ConcurrentHashMap<>();
    }

    public final void add(final Channel channel) {
        this.group.add(channel);
    }

    public final void remove(final Channel channel) {
        this.group.remove(channel);

        final MocoGroup group = this.channelGroups.get(channel);
        if (group != null) {
            this.channelGroups.remove(channel);
            Set<Channel> channels = this.groupChannels.get(group);
            if (channels != null) {
                channels.remove(channel);
            }
        }
    }

    public final void writeAndFlush(final Object message, final MocoGroup group) {
        if (group == null) {
            this.group.writeAndFlush(message);
            return;
        }

        final Collection<Channel> channels = this.groupChannels.getOrDefault(group, Set.of());
        for (Channel channel : channels) {
            channel.writeAndFlush(message);
        }
    }

    public final void join(final MocoGroup group, final Channel channel) {
        this.groupChannels.computeIfAbsent(group, key -> ConcurrentHashMap.newKeySet()).add(channel);
        this.channelGroups.put(channel, group);
    }
}
