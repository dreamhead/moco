package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.SocketRequest;
import com.github.dreamhead.moco.SocketResponse;
import com.github.dreamhead.moco.SocketResponseSetting;
import com.github.dreamhead.moco.model.DefaultSocketRequest;
import com.github.dreamhead.moco.model.DefaultSocketResponse;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.collect.ImmutableList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static io.netty.channel.ChannelHandler.Sharable;
import static java.lang.String.format;

@Sharable
public class MocoSocketHandler extends SimpleChannelInboundHandler<String> {
    private final ImmutableList<Setting<SocketResponseSetting>> settings;
    private final Setting<SocketResponseSetting> anySetting;
    private final MocoMonitor monitor;

    public MocoSocketHandler(final ActualSocketServer server) {
        this.settings = server.getSettings();
        this.anySetting = server.getAnySetting();
        this.monitor = server.getMonitor();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
        try {
            MessageContent content = content(msg);
            SocketRequest request = new DefaultSocketRequest(content);
            this.monitor.onMessageArrived(request);
            SocketResponse response = new DefaultSocketResponse();
            handleSession(new SessionContext(request, response));
            this.monitor.onMessageLeave(response);
            ctx.write(response.getContent().toString());
        } catch (Exception e) {
            this.monitor.onException(e);
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleSession(final SessionContext context) {
        for (Setting setting : settings) {
            if (setting.match(context.getRequest())) {
                setting.writeToResponse(context);
                return;
            }
        }

        if (anySetting.match(context.getRequest())) {
            anySetting.writeToResponse(context);
            return;
        }

        this.monitor.onUnexpectedMessage(context.getRequest());
        throw new RuntimeException(format("No handler found for request: %s", context.getRequest().getContent()));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        monitor.onException(cause);
    }
}
