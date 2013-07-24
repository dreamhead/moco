package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.List;

public class MocoHandler extends SimpleChannelInboundHandler<Object> {
    private final EventBus eventBus = new EventBus();

    private final List<BaseSetting> settings;
    private final RequestMatcher anyRequestMatcher;
    private final ResponseHandler anyResponseHandler;

    public MocoHandler(ActualHttpServer server) {
        this.settings = server.getSettings();
        this.anyRequestMatcher = server.getAnyRequestMatcher();
        this.anyResponseHandler = server.getAnyResponseHandler();
        this.eventBus.register(new MocoEventListener());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        if (message instanceof HttpRequest) {
            eventBus.post(message);
            httpRequestReceived(ctx, (HttpRequest)message);
        }
    }

    private void httpRequestReceived(ChannelHandlerContext ctx, HttpRequest request) {
        HttpResponse response = getResponse(request);
        eventBus.post(response);
        ctx.writeAndFlush(response);
        ctx.disconnect();
        ctx.close();
    }


//    @Override
//    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//        Object message = e.getMessage();
//
//        if (message instanceof HttpRequest) {
//            eventBus.post(message);
//            httpRequestReceived((HttpRequest) message, e.getChannel());
//        }
//    }

//    private void httpRequestReceived(HttpRequest request, Channel channel) {
//        HttpResponse response = getResponse(request);
//        eventBus.post(response);
//        channel.write(response);
//        channel.disconnect();
//        channel.close();
//    }

    private HttpResponse getResponse(HttpRequest request) {
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

    private HttpResponse doGetHttpResponse(HttpRequest request) {
        HttpResponse response = defaultResponse(request, HttpResponseStatus.OK);

        for (BaseSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(request, response);
                return response;
            }
        }

        if (anyResponseHandler != null) {
            if (anyRequestMatcher.match(request)) {
                anyResponseHandler.writeToResponse(request, response);
                return response;
            }
        }

        return defaultResponse(request, HttpResponseStatus.BAD_REQUEST);
    }

    private HttpResponse defaultResponse(HttpRequest request, HttpResponseStatus status) {
        return new DefaultFullHttpResponse(request.getProtocolVersion(), status);
    }
}
