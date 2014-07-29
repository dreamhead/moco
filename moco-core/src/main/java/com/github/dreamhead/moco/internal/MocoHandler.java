package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.google.common.collect.ImmutableList;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static com.github.dreamhead.moco.model.DefaultMutableHttpResponse.newResponse;
import static io.netty.handler.codec.http.HttpHeaders.*;


public class MocoHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ImmutableList<HttpSetting> settings;
    private final HttpSetting anySetting;
    private final MocoMonitor monitor;

    public MocoHandler(ActualHttpServer server) {
        this.settings = server.getSettings();
        this.anySetting = server.getAnySetting();
        this.monitor = server.getMonitor();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest message) throws Exception {
    	FullHttpResponse response = handleRequest(message);
        closeIfNotKeepAlive(message, ctx.writeAndFlush(response));
    }

    private FullHttpResponse handleRequest(FullHttpRequest message) {
        HttpRequest request = DefaultHttpRequest.newRequest(message);
        FullHttpResponse response = getHttpResponse(request).toFullResponse();
        prepareForKeepAlive(message, response);
        monitor.onMessageLeave(response);
        return response;
    }

    private DefaultMutableHttpResponse getHttpResponse(HttpRequest request) {
        try {
            monitor.onMessageArrived(request);
            return doGetHttpResponse(request);
        } catch (RuntimeException e) {
            monitor.onException(e);
            return newResponse(request, 400);
        } catch (Exception e) {
            monitor.onException(e);
            return newResponse(request, 500);
        }
    }

    private DefaultMutableHttpResponse doGetHttpResponse(HttpRequest request) {
        DefaultMutableHttpResponse httpResponse = newResponse(request, 200);
        SessionContext context = new SessionContext(request, httpResponse);

        for (HttpSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(context);
                return httpResponse;
            }
        }

        if (anySetting.match(request)) {
            anySetting.writeToResponse(context);
            return httpResponse;
        }

        monitor.onUnexpectedMessage(request);
        return newResponse(request, 400);
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
        return new DefaultFullHttpResponse(HttpVersion.valueOf(request.getVersion().text()), status);
    }
}
