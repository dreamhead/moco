package com.github.dreamhead.moco.util;

import io.netty.buffer.ByteBuf;

public class ByteBufs {
    public static byte[] toByteArray(final ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        int readerIndex = buf.readerIndex();
        buf.getBytes(readerIndex, bytes);
        return bytes;
    }

    private ByteBufs() {
    }
}
