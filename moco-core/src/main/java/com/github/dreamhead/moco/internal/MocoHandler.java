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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.util.HttpHeaders.UPGRADE;
import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpUtil.setKeepAlive;

@Sharable
public final class MocoHandler extends SimpleChannelInboundHandler<Object> {
    private final ActualHttpServer server;
    private final RequestHandler requestHandler;
    private final WebSocketHandler websocketHandler;
    private final SseStreamer sseStreamer;

    public MocoHandler(final ActualHttpServer server) {
        this.server = server;
        this.requestHandler = new RequestHandler(server);
        this.websocketHandler = new WebSocketHandler(server.getWebsocketServer());
        this.sseStreamer = new SseStreamer(server);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        websocketHandler.disconnect(ctx);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object message) {
        if (message instanceof FullHttpRequest request) {
            handleHttpRequest(ctx, request);
            return;
        }

        if (message instanceof WebSocketFrame frame) {
            websocketHandler.handleFrame(ctx, frame);
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

        Client client = requestHandler.createClient(ctx);
        DefaultMutableHttpResponse httpResponse = requestHandler.handleRequest(request, client);

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
        ctx.executor().execute(() -> streamSseEvents(ctx, httpResponse.getSseEvents().iterator()));
    }

    private void streamSseEvents(final ChannelHandlerContext ctx,
                                 final Iterator<SseEvent> events) {
        sseStreamer.streamEvents(ctx, events, new SseStreamer.SseEventWriter() {
            @Override
            public void writeEvent(final ChannelHandlerContext c, final SseEvent event) {
                c.writeAndFlush(new DefaultHttpContent(
                        Unpooled.copiedBuffer(event.toEventString(), StandardCharsets.UTF_8)
                ));
            }

            @Override
            public void finishStream(final ChannelHandlerContext c) {
                if (c.channel().isActive()) {
                    c.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
                     .addListener(ChannelFutureListener.CLOSE);
                }
            }
        });
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
