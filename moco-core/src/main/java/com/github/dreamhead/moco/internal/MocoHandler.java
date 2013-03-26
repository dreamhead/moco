package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.setting.BaseSetting;
import com.google.common.eventbus.EventBus;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.*;

import java.util.ArrayList;
import java.util.List;

public class MocoHandler extends SimpleChannelHandler {
    private static final DefaultHttpResponse DEFAULT_HTTP_RESPONSE = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);

    private EventBus eventBus = new EventBus();

    private List<BaseSetting> settings = new ArrayList<BaseSetting>();
    private ResponseHandler anyResponseHandler;

    public MocoHandler(List<BaseSetting> settings, ResponseHandler anyResponseHandler) {
        this.settings = settings;
        this.anyResponseHandler = anyResponseHandler;
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
            return DEFAULT_HTTP_RESPONSE;
        }
    }

    private HttpResponse doGetHttpResponse(HttpRequest request) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        for (BaseSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(request, response);
                return response;
            }
        }

        if (anyResponseHandler != null) {
            anyResponseHandler.writeToResponse(request, response);
            return response;
        }

        return DEFAULT_HTTP_RESPONSE;
    }
}
