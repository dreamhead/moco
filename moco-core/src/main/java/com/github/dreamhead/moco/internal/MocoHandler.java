package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.model.LazyHttpRequest;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.collect.ImmutableList;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaders.*;

public class MocoHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ImmutableList<BaseSetting> settings;
    private final BaseSetting anySetting;
    private final MocoMonitor monitor;

    public MocoHandler(ActualHttpServer server) {
        this.settings = server.getSettings();
        this.anySetting = server.getAnySetting();
        this.monitor = server.getMonitor();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest message) throws Exception {
        closeIfNotKeepAlive(message, ctx.writeAndFlush(handleRequest(message)));
    }

    private FullHttpResponse handleRequest(FullHttpRequest message) {
        FullHttpResponse response = getHttpResponse(message);
        prepareForKeepAlive(message, response);
        monitor.onMessageLeave(response);
        return response;
    }

    private FullHttpResponse getHttpResponse(FullHttpRequest message) {
        try {
            return doGetFullHttpResponse(message);
        } catch (RuntimeException e) {
            monitor.onException(e);
            return defaultResponse(message, HttpResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            monitor.onException(e);
            return defaultResponse(message, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private FullHttpResponse doGetFullHttpResponse(FullHttpRequest request) {
        FullHttpResponse response = defaultResponse(request, HttpResponseStatus.OK);
        LazyHttpRequest httpRequest = new LazyHttpRequest(request);
        monitor.onMessageArrived(httpRequest);
        SessionContext context = new SessionContext(httpRequest, request, response);

        for (BaseSetting setting : settings) {
            if (setting.match(httpRequest)) {
                setting.writeToResponse(context);
                return response;
            }
        }

        if (anySetting.match(httpRequest)) {
            anySetting.writeToResponse(context);
            return response;
        }

        monitor.onUnexpectedMessage(request);
        return defaultResponse(request, HttpResponseStatus.BAD_REQUEST);
    }

    private void closeIfNotKeepAlive(FullHttpRequest request, ChannelFuture future) {
        if (!isKeepAlive(request)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void prepareForKeepAlive(FullHttpRequest request, FullHttpResponse response) {
        if (isKeepAlive(request)) {
            setKeepAlive(response, true);
            setContentLengthForKeepAlive(response);
        }
    }

    private void setContentLengthForKeepAlive(FullHttpResponse response) {
        if (!isContentLengthSet(response)) {
            setContentLength(response, response.content().writerIndex());
        }
    }

    private FullHttpResponse defaultResponse(HttpRequest request, HttpResponseStatus status) {
        return new DefaultFullHttpResponse(request.getProtocolVersion(), status);
    }
}
