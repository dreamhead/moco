package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.WebSocketServer;
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

public class ActualWebSocketServer implements WebSocketServer {
    private Resource connected;
    private ChannelGroup group;
    private String uri;

    public ActualWebSocketServer(final String uri) {
        this.uri = uri;
        this.group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public final void connected(final Resource resource) {
        this.connected = resource;
    }

    private void connect(final Channel channel) {
        this.group.add(channel);
    }

    public final void disconnect(final Channel channel) {
        this.group.remove(channel);
    }

    public final String getUri() {
        return uri;
    }

    private void sendConnected(final Channel channel) {
        if (connected != null) {
            MessageContent messageContent = this.connected.readFor(null);
            channel.writeAndFlush(new TextWebSocketFrame(messageContent.toString()));
        }
    }

    public final void connectRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getUri(), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        Channel channel = ctx.channel();
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
            return;
        }

        handshaker.handshake(channel, request);
        connect(channel);
        sendConnected(channel);
    }
}
