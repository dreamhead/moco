package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.util.Strings;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.net.HttpHeaders.UPGRADE;
import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpUtil.setKeepAlive;

@Sharable
public final class MocoHandler extends SimpleChannelInboundHandler<Object> {
    private final ActualHttpServer server;
    private final HttpHandler httpHandler;
    private final WebSocketHandler websocketHandler;

    public MocoHandler(final ActualHttpServer server) {
        this.server = server;
        this.httpHandler = new HttpHandler(server);
        this.websocketHandler = new WebSocketHandler(server.getWebsocketServer());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        websocketHandler.disconnect(ctx);
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

        if (upgradeWebsocket(request)) {
            websocketHandler.connect(ctx, request);
            return;
        }

        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        Client client = new Client(address);
        DefaultMutableHttpResponse httpResponse = httpHandler.handleRequest(request, client);

        if (httpResponse.isSse()) {
            streamSseResponse(ctx, httpResponse);
            return;
        }

        FullHttpResponse response = httpResponse.toFullResponse();
        prepareForKeepAlive(request, response);
        closeIfNotKeepAlive(request, ctx.write(response));
    }

    private void streamSseResponse(final ChannelHandlerContext ctx,
                                    final DefaultMutableHttpResponse httpResponse) {
        HttpResponse headerResponse = new DefaultHttpResponse(
                HttpVersion.valueOf(httpResponse.getVersion().text()),
                HttpResponseStatus.valueOf(httpResponse.getStatus()));

        for (Map.Entry<String, String[]> entry : httpResponse.getHeaders().entrySet()) {
            for (String value : entry.getValue()) {
                headerResponse.headers().add(entry.getKey(), value);
            }
        }

        ctx.write(headerResponse);

        List<SseEvent> events = httpResponse.getSseEvents();
        streamEvents(ctx, events, 0);
    }

    private void streamEvents(final ChannelHandlerContext ctx, final List<SseEvent> events, final int index) {
        if (!ctx.channel().isActive() || index >= events.size()) {
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
               .addListener(ChannelFutureListener.CLOSE);
            return;
        }

        SseEvent event = events.get(index);
        ChannelFuture future = ctx.writeAndFlush(new DefaultHttpContent(
                Unpooled.copiedBuffer(event.toEventString(), StandardCharsets.UTF_8)
        ));

        int delay = event.getDelay();
        if (delay > 0) {
            ctx.executor().schedule(
                    () -> streamEvents(ctx, events, index + 1),
                    delay, TimeUnit.MILLISECONDS);
        } else {
            future.addListener(f -> streamEvents(ctx, events, index + 1));
        }
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

    private void prepareForKeepAlive(final FullHttpRequest request, final FullHttpResponse response) {
        if (isKeepAlive(request)) {
            setKeepAlive(response, true);
            setContentLength(response, response.content().writerIndex());
        }
    }

    private void closeIfNotKeepAlive(final FullHttpRequest request, final ChannelFuture future) {
        if (!isKeepAlive(request)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        server.onException(cause);
    }
}
