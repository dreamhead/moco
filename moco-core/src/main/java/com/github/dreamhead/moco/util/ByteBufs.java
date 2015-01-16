package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class ByteBufs {
    public static MessageContent toMessageContent(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        int readerIndex = buf.readerIndex();
        buf.getBytes(readerIndex, bytes);

        return content()
                .withContent(bytes)
                .build();
    }

    private ByteBufs() {}
}
