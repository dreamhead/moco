package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.util.ByteBufs;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketBroadcastHandler implements ResponseHandler {
    private final String content;

    public WebSocketBroadcastHandler(final String content) {
        this.content = content;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        ByteBuf byteBuf = ByteBufs.toByteBuf(content.getBytes());
        context.getGroup().writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }
}
