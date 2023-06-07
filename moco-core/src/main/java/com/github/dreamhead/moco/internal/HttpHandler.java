package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.net.InetSocketAddress;

import static com.github.dreamhead.moco.model.DefaultMutableHttpResponse.newResponse;
import static io.netty.handler.codec.http.HttpUtil.isContentLengthSet;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpUtil.setKeepAlive;

public class HttpHandler {
    private static final int DEFAULT_STATUS = HttpResponseStatus.OK.code();

    private final ActualHttpServer server;

    public HttpHandler(final ActualHttpServer server) {
        this.server = server;
    }

    public final FullHttpResponse handleRequest(final ChannelHandlerContext ctx, final FullHttpRequest message) {
        final Channel channel = ctx.channel();
        final InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        HttpRequest request = DefaultHttpRequest.newRequest(message, new Client(address));
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

    private void setContentLengthForKeepAlive(final FullHttpResponse response) {
        if (!isContentLengthSet(response)) {
            setContentLength(response, response.content().writerIndex());
        }
    }

    private void prepareForKeepAlive(final FullHttpRequest request, final FullHttpResponse response) {
        if (isKeepAlive(request)) {
            setKeepAlive(response, true);
            setContentLengthForKeepAlive(response);
        }
    }
}
