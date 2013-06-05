package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.eventbus.EventBus;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

public class MocoHandler extends SimpleChannelHandler {
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
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();

        if (message instanceof HttpRequest) {
            eventBus.post(message);
            httpRequestReceived((HttpRequest) message, e.getChannel());
        }
    }

    private void httpRequestReceived(HttpRequest request, Channel channel) {
        HttpResponse response = getResponse(request);
        eventBus.post(response);
        channel.write(response);
        channel.disconnect();
        channel.close();
    }

    private HttpResponse getResponse(HttpRequest request) {
        try {
            return doGetHttpResponse(request);
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
        return new DefaultHttpResponse(request.getProtocolVersion(), status);
    }
}
