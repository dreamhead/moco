package com.github.moco.request;

import com.github.moco.MocoServer;
import com.github.moco.RequestSetting;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

public abstract class BaseRequestSetting extends RequestSetting {
    public abstract boolean match(HttpRequest request);

    public BaseRequestSetting(MocoServer server) {
        super(server);
    }

    @Override
    protected void addToServer(MocoServer server) {
        server.addRequestSettings(this);
    }

    public boolean isMatchAny() {
        return false;
    }

    public void handleRequest(HttpRequest request, Channel channel) {
        if (handler == null) {
            return;
        }

        handler.handle(request, channel);
    }

    public void writeResponse(ChannelBuffer buffer) {
        buffer.writeBytes(response.getBytes());
    }
}
