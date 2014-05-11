package com.github.dreamhead.moco.util;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class ByteBufs {
    public static byte[] asBytes(ByteBuf content) {
        if (content.hasArray()) {
            return content.array();
        }

        if (content.nioBufferCount() > 0) {
            ByteBuffer byteBuffer = content.nioBuffer();
            byte[] bytes = new byte[byteBuffer.capacity()];
            byteBuffer.get(bytes);
            return bytes;
        }

        throw new IllegalArgumentException("unknown content");
    }

    private ByteBufs() {}
}
