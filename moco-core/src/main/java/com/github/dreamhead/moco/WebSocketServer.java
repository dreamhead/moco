package com.github.dreamhead.moco;

import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

public class WebSocketServer {
    private Resource open;
    private ChannelGroup group;
    private String uri;

    public WebSocketServer(final String uri) {
        this.uri = uri;
        this.group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void open(final Resource resource) {
        this.open = resource;
    }

    public void addChannel(final Channel channel) {
//        if (open != null) {
//            MessageContent messageContent = this.open.readFor(null);
//            channel.writeAndFlush(messageContent.getContent());
//        }
        this.group.add(channel);
    }

    public void removeChannel(final Channel channel) {
        this.group.remove(channel);
    }

    public String getUri() {
        return uri;
    }

    public void sendOpen(final Channel channel) {
        if (open != null) {
            MessageContent messageContent = this.open.readFor(null);
            channel.writeAndFlush(new TextWebSocketFrame(messageContent.toString()));
        }
    }

    public void connectRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getUri(), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
            sendOpen(ctx.channel());
        }
    }
}
