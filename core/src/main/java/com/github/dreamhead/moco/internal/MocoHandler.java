package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.setting.BaseSetting;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.*;

import java.util.ArrayList;
import java.util.List;

public class MocoHandler extends SimpleChannelHandler {
    private List<BaseSetting> settings = new ArrayList<BaseSetting>();
    private ResponseHandler anyResponseHandler;

    public MocoHandler(List<BaseSetting> settings, ResponseHandler anyResponseHandler) {
        this.settings = settings;
        this.anyResponseHandler = anyResponseHandler;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();

        if (message instanceof HttpRequest) {
            httpRequestReceived((HttpRequest) message, e.getChannel());
        }
    }

    private void httpRequestReceived(HttpRequest request, Channel channel) {
        channel.write(getResponse(request));

        channel.disconnect();
        channel.close();
    }

    private HttpResponse getResponse(HttpRequest request) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        for (BaseSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(response);
                return response;
            }
        }

        if (anyResponseHandler != null) {
            anyResponseHandler.writeToResponse(response);
            return response;
        }

        return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
    }
}
