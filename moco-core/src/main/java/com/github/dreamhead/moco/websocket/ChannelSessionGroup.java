package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.recorder.MocoGroup;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ChannelSessionGroup {
    private final ChannelGroup group;
    private Multimap<MocoGroup, Channel> groupChannels;
    private Map<Channel, MocoGroup> channelGroups;

    public ChannelSessionGroup(final ChannelGroup group) {
        this.group = group;
        this.groupChannels = HashMultimap.create();
        this.channelGroups = newHashMap();
    }

    public void add(final Channel channel) {
        this.group.add(channel);
    }

    public void remove(final Channel channel) {
        this.group.remove(channel);

        final MocoGroup group = this.channelGroups.get(channel);
        if (group != null) {
            this.channelGroups.remove(channel);
            this.groupChannels.remove(group, channel);
        }
    }

    public void writeAndFlush(final Object message, final MocoGroup group) {
        if (group == null) {
            this.group.writeAndFlush(message);
            return;
        }

        final Collection<Channel> channels = this.groupChannels.get(group);
        for (Channel channel : channels) {
            channel.writeAndFlush(message);
        }
    }

    public void join(final MocoGroup group, final Channel channel) {
        this.groupChannels.put(group, channel);
        this.channelGroups.put(channel, group);
    }
}
