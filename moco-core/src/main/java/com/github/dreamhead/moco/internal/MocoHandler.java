package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoMonitor;
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
        monitor.onMessageArrived(message);
        FullHttpResponse response = getResponse(message);
        prepareForKeepAlive(message, response);
        monitor.onMessageLeave(response);
        closeIfNotKeepAlive(message, ctx.writeAndFlush(response));
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

    private FullHttpResponse getResponse(FullHttpRequest request) {
        try {
            return doGetHttpResponse(request);
        } catch (RuntimeException e) {
            monitor.onException(e);
            return defaultResponse(request, HttpResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            monitor.onException(e);
            return defaultResponse(request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private FullHttpResponse doGetHttpResponse(FullHttpRequest request) {
        FullHttpResponse response = defaultResponse(request, HttpResponseStatus.OK);
        SessionContext context = new SessionContext(request, response);

        for (BaseSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(context);
                return response;
            }
        }

        if (anySetting.match(request)) {
            anySetting.writeToResponse(context);
            return response;
        }

        monitor.onUnexpectedMessage(request);
        return defaultResponse(request, HttpResponseStatus.BAD_REQUEST);
    }

    private FullHttpResponse defaultResponse(HttpRequest request, HttpResponseStatus status) {
        return new DefaultFullHttpResponse(request.getProtocolVersion(), status);
    }
}
