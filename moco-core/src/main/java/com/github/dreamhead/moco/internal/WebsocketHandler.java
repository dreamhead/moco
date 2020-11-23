package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.util.ByteBufs;
import com.github.dreamhead.moco.websocket.ActualWebSocketServer;
import com.github.dreamhead.moco.websocket.WebsocketResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.Optional;

public class WebsocketHandler {
    private final ActualWebSocketServer websocketServer;

    public WebsocketHandler(final ActualWebSocketServer websocketServer) {
        this.websocketServer = websocketServer;
    }

    public void handleFrame(final ChannelHandlerContext ctx,
                            final WebSocketFrame message) {
        Optional<WebSocketFrame> frame = getResponseFrame(ctx, message);
        frame.ifPresent(webSocketFrame -> ctx.channel().writeAndFlush(webSocketFrame));
    }

    private Optional<WebSocketFrame> getResponseFrame(final ChannelHandlerContext ctx,
                                                      final WebSocketFrame message) {
        if (message instanceof PingWebSocketFrame) {
            return Optional.of(websocketServer.handlePingPong((PingWebSocketFrame) message));
        }

        Optional<WebsocketResponse> response = websocketServer.handleRequest(ctx, message);
        if (response.isPresent()) {
            ByteBuf byteBuf = ByteBufs.toByteBuf(response.get().getContent().getContent());
            return Optional.of(new BinaryWebSocketFrame(byteBuf));
        }

        return Optional.empty();
    }

    public void connect(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        if (websocketServer != null) {
            websocketServer.connectRequest(ctx, request);
        }
    }

    void disconnect(final ChannelHandlerContext ctx) {
        if (websocketServer != null) {
            websocketServer.disconnect(ctx.channel());
        }
    }
}
