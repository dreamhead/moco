package com.github.dreamhead.moco.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class ByteBufs {
    public static byte[] toByteArray(final ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        int readerIndex = buf.readerIndex();
        buf.getBytes(readerIndex, bytes);
        return bytes;
    }

    public static ByteBuf toByteBuf(final byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes);
    }

    private ByteBufs() {
    }
}
