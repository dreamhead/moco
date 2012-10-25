package com.github.moco.internal;

import com.github.moco.ResponseHandler;
import com.github.moco.setting.BaseSetting;
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

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();

        if (message instanceof HttpRequest) {
            Channel channel = e.getChannel();

            writeResponse(channel, (HttpRequest) message);

            channel.disconnect();
            channel.close();
        }
    }

    private void writeResponse(Channel channel, HttpRequest request) {
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

    public void setAnyResponseHandler(ResponseHandler handler) {
        this.anyResponseHandler = handler;
    }

    public void addSetting(BaseSetting setting) {
        this.settings.add(setting);
    }
}
