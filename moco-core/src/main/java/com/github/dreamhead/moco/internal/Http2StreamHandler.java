package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.sse.SseEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Handler for processing individual HTTP/2 streams.
 *
 * <p>This handler is created for each HTTP/2 stream when using Http2MultiplexHandler.
 * It processes HTTP/2 frames and converts them to Moco's request/response model.
 */
public class Http2StreamHandler extends SimpleChannelInboundHandler<Http2StreamFrame> {

    private static final Logger logger = LoggerFactory.getLogger(Http2StreamHandler.class);

    private final RequestHandler requestHandler;
    private final SseStreamer sseStreamer;

    private Http2Headers headers;
    private final StringBuilder requestBodyBuilder = new StringBuilder();

    public Http2StreamHandler(final ActualHttpServer server) {
        this.requestHandler = new RequestHandler(server);
        this.sseStreamer = new SseStreamer(server);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Http2StreamFrame frame) {
        if (frame instanceof Http2HeadersFrame) {
            handleHeadersFrame(ctx, (Http2HeadersFrame) frame);
        } else if (frame instanceof Http2DataFrame) {
            handleDataFrame(ctx, (Http2DataFrame) frame);
        }
    }

    private void handleHeadersFrame(final ChannelHandlerContext ctx, final Http2HeadersFrame headersFrame) {
        this.headers = headersFrame.headers();

        if (headersFrame.isEndStream()) {
            processRequest(ctx);
        }
    }

    private void handleDataFrame(final ChannelHandlerContext ctx, final Http2DataFrame dataFrame) {
        ByteBuf content = dataFrame.content();
        requestBodyBuilder.append(content.toString(StandardCharsets.UTF_8));

        if (dataFrame.isEndStream()) {
            processRequest(ctx);
        }
    }

    private void processRequest(final ChannelHandlerContext ctx) {
        if (headers == null) {
            logger.error("Received data frame without headers");
            return;
        }

        try {
            String body = requestBodyBuilder.toString();

            FullHttpRequest request = convertToMocoRequest(headers, body);
            Client client = requestHandler.createClient(ctx);
            DefaultMutableHttpResponse httpResponse = requestHandler.handleRequest(request, client);

            sendResponse(ctx, httpResponse);

        } catch (Exception e) {
            logger.error("Error processing HTTP/2 request", e);
            sendErrorResponse(ctx);
        } finally {
            headers = null;
            requestBodyBuilder.setLength(0);
        }
    }

    private FullHttpRequest convertToMocoRequest(final Http2Headers http2Headers, final String body) {
        String method = http2Headers.method() != null ? http2Headers.method().toString() : "GET";
        String uri = http2Headers.path() != null ? http2Headers.path().toString() : "/";

        FullHttpRequest request = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1,
            HttpMethod.valueOf(method),
            uri,
            Unpooled.copiedBuffer(body, StandardCharsets.UTF_8)
        );

        http2Headers.forEach(entry -> {
            String name = entry.getKey().toString();
            if (!name.startsWith(":")) {
                String value = entry.getValue().toString();
                request.headers().add(name, value);
            }
        });

        return request;
    }

    private void sendResponse(final ChannelHandlerContext ctx, final DefaultMutableHttpResponse httpResponse) {
        try {
            if (httpResponse.isSse()) {
                streamSseResponse(ctx, httpResponse);
                return;
            }

            Http2Headers responseHeaders = toHttp2Headers(httpResponse);
            ctx.write(new DefaultHttp2HeadersFrame(responseHeaders));

            ByteBuf content = toByteBuf(httpResponse.getContent());
            ctx.writeAndFlush(new DefaultHttp2DataFrame(content, true));

        } catch (Exception e) {
            logger.error("Error sending HTTP/2 response", e);
        }
    }

    private void streamSseResponse(final ChannelHandlerContext ctx, final DefaultMutableHttpResponse httpResponse) {
        try {
            Http2Headers responseHeaders = toHttp2Headers(httpResponse);
            ctx.write(new DefaultHttp2HeadersFrame(responseHeaders));

            sseStreamer.streamEvents(ctx, httpResponse.getSseEvents().iterator(), new SseStreamer.SseEventWriter() {
                @Override
                public void writeEvent(final ChannelHandlerContext c, final SseEvent event) {
                    ByteBuf content = Unpooled.copiedBuffer(event.toEventString(), StandardCharsets.UTF_8);
                    c.writeAndFlush(new DefaultHttp2DataFrame(content, false));
                }

                @Override
                public void finishStream(final ChannelHandlerContext c) {
                    if (c.channel().isActive()) {
                        c.writeAndFlush(new DefaultHttp2DataFrame(Unpooled.EMPTY_BUFFER, true));
                    }
                }
            });

        } catch (Exception e) {
            logger.error("Error streaming HTTP/2 SSE response", e);
        }
    }

    private ByteBuf toByteBuf(final MessageContent content) {
        if (content != null && content.hasContent()) {
            return Unpooled.wrappedBuffer(content.getContent());
        }
        return Unpooled.EMPTY_BUFFER;
    }

    private static final Set<String> HTTP2_DISALLOWED_HEADERS = Set.of(
            "connection", "keep-alive", "proxy-connection",
            "transfer-encoding", "upgrade",
            "content-length"
    );

    private Http2Headers toHttp2Headers(final DefaultMutableHttpResponse response) {
        Http2Headers headers = new DefaultHttp2Headers();

        headers.status(String.valueOf(response.getStatus()));

        for (String name : response.getHeaders().keySet()) {
            if (HTTP2_DISALLOWED_HEADERS.contains(name.toLowerCase())) {
                continue;
            }
            for (String value : response.getHeaders().get(name)) {
                headers.set(name.toLowerCase(), value);
            }
        }

        return headers;
    }

    private void sendErrorResponse(final ChannelHandlerContext ctx) {
        Http2Headers headers = new DefaultHttp2Headers();
        headers.status("500");
        ctx.writeAndFlush(new DefaultHttp2HeadersFrame(headers, true));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        logger.error("Exception in HTTP/2 stream handler", cause);
        ctx.close();
    }
}
