package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AbstractResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.ByteBufs;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketBroadcastHandler extends AbstractResponseHandler {
    private final Resource content;

    public WebSocketBroadcastHandler(final Resource content) {
        this.content = content;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        MessageContent content = this.content.readFor(context.getRequest());
        ByteBuf byteBuf = ByteBufs.toByteBuf(content.getContent());
        context.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    @Override
    public ResponseHandler doApply(final MocoConfig config) {
        Resource applied = content.apply(config);
        if (applied == content) {
            return this;
        }

        return new WebSocketBroadcastHandler(applied);
    }
}
