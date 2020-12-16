package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AbstractResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.recorder.MocoGroup;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.ByteBufs;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketBroadcastHandler extends AbstractResponseHandler {
    private final Resource content;
    private final MocoGroup group;

    public WebSocketBroadcastHandler(final Resource content, final MocoGroup group) {
        this.content = content;
        this.group = group;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        MessageContent content = this.content.readFor(context.getRequest());
        ByteBuf byteBuf = ByteBufs.toByteBuf(content.getContent());
        context.writeAndFlush(new BinaryWebSocketFrame(byteBuf), group);
    }

    @Override
    public ResponseHandler doApply(final MocoConfig config) {
        Resource applied = content.apply(config);
        if (applied == content) {
            return this;
        }

        return new WebSocketBroadcastHandler(applied, group);
    }
}
