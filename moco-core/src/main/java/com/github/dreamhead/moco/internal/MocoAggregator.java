package com.github.dreamhead.moco.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public final class MocoAggregator extends ChannelInboundHandlerAdapter {
    private CompositeByteBuf bufs =  ByteBufAllocator.DEFAULT.compositeBuffer();

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            bufs.addComponent(buf);
            bufs.writerIndex(bufs.writerIndex() + buf.writerIndex());
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        if (bufs.numComponents() > 0) {
            ctx.fireChannelRead(bufs);
            bufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        }

        ctx.fireChannelReadComplete();
    }
}
