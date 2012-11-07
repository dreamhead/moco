package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.setting.BaseSetting;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

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
        writeResponse(request, channel);

        channel.disconnect();
        channel.close();
    }

    private void writeResponse(HttpRequest request, Channel channel) {
        for (BaseSetting setting : settings) {
            if (setting.match(request)) {
                setting.handle(channel);
                return;
            }
        }

        if (anyResponseHandler != null) {
            anyResponseHandler.writeToResponse(channel);
        } else {
            channel.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
        }
    }
}
