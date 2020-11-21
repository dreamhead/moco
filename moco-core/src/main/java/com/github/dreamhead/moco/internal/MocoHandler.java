package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.util.Strings;
import com.github.dreamhead.moco.websocket.ActualWebSocketServer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
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
    private final WebsocketHandler websocketHandler;

    public MocoHandler(final ActualHttpServer server) {
        this.server = server;
        this.websocketServer = server.getWebsocketServer();
        this.websocketHandler = new WebsocketHandler(websocketServer);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
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
            websocketHandler.handleFrame(ctx, (WebSocketFrame) message);
        }
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        if (!request.decoderResult().isSuccess()) {
            ctx.writeAndFlush(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        if (!upgradeWebsocket(request)) {
            FullHttpResponse response = handleRequest(request);
            closeIfNotKeepAlive(request, ctx.write(response));
            return;
        }

        websocketHandler.connect(ctx, request);
    }

    private boolean upgradeWebsocket(final FullHttpRequest request) {
        if (io.netty.handler.codec.http.HttpMethod.GET.equals(request.method())) {
            String upgrade = request.headers().get(UPGRADE);
            return "websocket".equals(Strings.strip(upgrade));
        }

        return false;
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
