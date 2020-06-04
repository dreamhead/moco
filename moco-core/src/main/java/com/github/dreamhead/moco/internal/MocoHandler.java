package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.util.ByteBufs;
import com.github.dreamhead.moco.util.Strings;
import com.github.dreamhead.moco.websocket.ActualWebSocketServer;
import com.github.dreamhead.moco.websocket.WebsocketResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import static com.github.dreamhead.moco.model.DefaultMutableHttpResponse.newResponse;
import static com.google.common.net.HttpHeaders.UPGRADE;
import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpUtil.isContentLengthSet;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpUtil.setKeepAlive;

@Sharable
public final class MocoHandler extends SimpleChannelInboundHandler<Object> {
    private static final int DEFAULT_STATUS = HttpResponseStatus.OK.code();
    private final ActualWebSocketServer websocketServer;
    private final ActualHttpServer server;

    public MocoHandler(final ActualHttpServer server) {
        this.server = server;
        this.websocketServer = server.getWebsocketServer();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        if (websocketServer != null) {
            websocketServer.disconnect(ctx.channel());
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object message) {
        if (message instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) message);
            return;
        }

        if (message instanceof WebSocketFrame) {
            handleWebsocketFrame(ctx, (WebSocketFrame) message);
        }
    }

    private void handleWebsocketFrame(final ChannelHandlerContext ctx, final WebSocketFrame message) {
        WebSocketFrame frame = getResponseFrame(ctx, message);
        ctx.channel().writeAndFlush(frame);
    }

    private WebSocketFrame getResponseFrame(final ChannelHandlerContext ctx, final WebSocketFrame message) {
        if (message instanceof PingWebSocketFrame) {
            return websocketServer.handlePingPong((PingWebSocketFrame) message);
        }

        WebsocketResponse response = websocketServer.handleRequest(ctx, message);
        ByteBuf byteBuf = ByteBufs.toByteBuf(response.getContent().getContent());
        return new BinaryWebSocketFrame(byteBuf);
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        if (!request.decoderResult().isSuccess() || !upgradeWebsocket(request)) {
            FullHttpResponse response = handleRequest(request);
            closeIfNotKeepAlive(request, ctx.write(response));
            return;
        }

        websocketServer.connectRequest(ctx, request);
    }

    private boolean upgradeWebsocket(final FullHttpRequest request) {
        String upgrade = request.headers().get(UPGRADE);
        return "websocket".equals(Strings.strip(upgrade));
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse handleRequest(final FullHttpRequest message) {
        HttpRequest request = DefaultHttpRequest.newRequest(message);
        DefaultMutableHttpResponse httpResponse = getHttpResponse(request);
        FullHttpResponse response = httpResponse.toFullResponse();
        prepareForKeepAlive(message, response);
        return response;
    }

    private DefaultMutableHttpResponse getHttpResponse(final HttpRequest request) {
        DefaultMutableHttpResponse httpResponse = newResponse(request, DEFAULT_STATUS);
        SessionContext context = new SessionContext(request, httpResponse);
        return doGetResponse(request, context);
    }

    private DefaultMutableHttpResponse doGetResponse(final HttpRequest request, final SessionContext context) {
        try {
            return (DefaultMutableHttpResponse) server.getResponse(context)
                    .orElse(newResponse(request, HttpResponseStatus.BAD_REQUEST.code()));
        } catch (RuntimeException e) {
            return newResponse(request, HttpResponseStatus.BAD_REQUEST.code());
        } catch (Exception e) {
            return newResponse(request, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
    }

    private void closeIfNotKeepAlive(final FullHttpRequest request, final ChannelFuture future) {
        if (!isKeepAlive(request)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void prepareForKeepAlive(final FullHttpRequest request, final FullHttpResponse response) {
        if (isKeepAlive(request)) {
            setKeepAlive(response, true);
            setContentLengthForKeepAlive(response);
        }
    }

    private void setContentLengthForKeepAlive(final FullHttpResponse response) {
        if (!isContentLengthSet(response)) {
            setContentLength(response, response.content().writerIndex());
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        server.onException(cause);
    }
}
