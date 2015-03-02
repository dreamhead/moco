package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class ByteBufs {
    public static MessageContent toMessageContent(ByteBuf buf) {
        return content()
                .withContent(new ByteBufInputStream(buf))
                .build();
    }

    private ByteBufs() {}
}
