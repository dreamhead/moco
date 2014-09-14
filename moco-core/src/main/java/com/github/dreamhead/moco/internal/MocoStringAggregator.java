package com.github.dreamhead.moco.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MocoStringAggregator extends SimpleChannelInboundHandler<String> {
    private StringBuilder sb = new StringBuilder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        this.sb.append(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (sb.length() > 0) {
            ctx.fireChannelRead(sb.toString());
            sb = new StringBuilder();
        }
    }
}
