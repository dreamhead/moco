package com.github.moco;

import com.github.moco.request.BaseRequestSetting;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
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
    private BaseRequestSetting anyRequestSetting;
    private List<BaseRequestSetting> requestSettings = new ArrayList<BaseRequestSetting>();

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
        for (BaseRequestSetting requestSetting : requestSettings) {
            if (requestSetting.match(request)) {
                channel.write(createResponse(requestSetting));
                return;
            }
        }

        if (anyRequestSetting != null) {
            channel.write(createResponse(anyRequestSetting));
        } else {
            channel.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
        }
    }

    private DefaultHttpResponse createResponse(BaseRequestSetting setting) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        setting.writeResponse(buffer);
        response.setContent(buffer);
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setHeader("Content-Length", response.getContent().writerIndex());
        return response;
    }

    public void addRequestSetting(BaseRequestSetting requestSetting) {
        if (requestSetting.isMatchAny()) {
            this.anyRequestSetting = requestSetting;
        } else {
            this.requestSettings.add(requestSetting);
        }
    }
}
