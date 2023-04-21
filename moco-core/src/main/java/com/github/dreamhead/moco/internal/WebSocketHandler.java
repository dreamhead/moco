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

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class WebSocketHandler {
    private final ActualWebSocketServer websocketServer;

    public WebSocketHandler(final ActualWebSocketServer websocketServer) {
        this.websocketServer = websocketServer;
    }

    public final void handleFrame(final ChannelHandlerContext ctx,
                                  final WebSocketFrame message) {
        if (this.websocketServer == null) {
            return;
        }

        Optional<WebSocketFrame> frame = getResponseFrame(ctx, message);
        frame.ifPresent(webSocketFrame -> ctx.channel().writeAndFlush(webSocketFrame));
    }

    private Optional<WebSocketFrame> getResponseFrame(final ChannelHandlerContext ctx,
                                                      final WebSocketFrame message) {
        final InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        final Client client = new Client(address);
        if (message instanceof PingWebSocketFrame) {
            return Optional.of(websocketServer.handlePingPong((PingWebSocketFrame) message,
                    client));
        }

        Optional<WebsocketResponse> response = websocketServer.handleRequest(ctx, message, client);
        return response.map(this::asWebsocketFrame);
    }

    private BinaryWebSocketFrame asWebsocketFrame(final WebsocketResponse actual) {
        ByteBuf byteBuf = ByteBufs.toByteBuf(actual.getContent().getContent());
        return new BinaryWebSocketFrame(byteBuf);
    }

    public final void connect(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        if (websocketServer != null) {
            websocketServer.connectRequest(ctx, request);
        }
    }

    public final void disconnect(final ChannelHandlerContext ctx) {
        if (websocketServer != null) {
            websocketServer.disconnect(ctx.channel());
        }
    }
}
