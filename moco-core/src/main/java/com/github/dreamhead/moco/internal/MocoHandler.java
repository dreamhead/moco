package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public class MocoHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final EventBus eventBus = new EventBus();

    private final ImmutableList<BaseSetting> settings;
    private final BaseSetting anySetting;

    public MocoHandler(ActualHttpServer server) {
        this.settings = server.getSettings();
        this.anySetting = server.getAnySetting();
        this.eventBus.register(new MocoEventListener());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest message) throws Exception {
        eventBus.post(message);
        httpRequestReceived(ctx, message);
    }

    private void httpRequestReceived(final ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpResponse response = getResponse(request);
        eventBus.post(response);
        ChannelFuture future = ctx.writeAndFlush(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    private FullHttpResponse getResponse(FullHttpRequest request) {
        try {
            return doGetHttpResponse(request);
        } catch (RuntimeException e) {
            eventBus.post(e);
            return defaultResponse(request, HttpResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            eventBus.post(e);
            return defaultResponse(request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private FullHttpResponse doGetHttpResponse(FullHttpRequest request) {
        FullHttpResponse response = defaultResponse(request, HttpResponseStatus.OK);

        for (BaseSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(request, response);
                return response;
            }
        }

        if (anySetting.match(request)) {
            anySetting.writeToResponse(request, response);
            return response;
        }

        return defaultResponse(request, HttpResponseStatus.BAD_REQUEST);
    }

    private FullHttpResponse defaultResponse(HttpRequest request, HttpResponseStatus status) {
        return new DefaultFullHttpResponse(request.getProtocolVersion(), status);
    }
}
