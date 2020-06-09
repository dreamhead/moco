package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.WebSocketServer;
import com.github.dreamhead.moco.internal.BaseActualServer;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.monitor.QuietMonitor;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.setting.Setting;
import com.github.dreamhead.moco.util.ByteBufs;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.text;

public final class ActualWebSocketServer
        extends BaseActualServer<WebsocketResponseSetting, ActualWebSocketServer>
        implements WebSocketServer {
    private Resource connected;
    private ChannelGroup group;
    private String uri;
    private List<PingPongSetting> settings;

    public ActualWebSocketServer(final String uri) {
        super(0, new QuietMonitor(), new MocoConfig[0]);
        this.uri = uri;
        this.group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.settings = new ArrayList<>();
    }

    public void connected(final Resource resource) {
        this.connected = resource;
    }

    @Override
    public PongResponse ping(final String message) {
        return this.ping(text(message));
    }

    @Override
    public PongResponse ping(final Resource message) {
        PingPongSetting setting = new PingPongSetting(message);
        settings.add(setting);
        return setting;
    }

    private void connect(final Channel channel) {
        this.group.add(channel);
    }

    public void disconnect(final Channel channel) {
        this.group.remove(channel);
    }

    public String getUri() {
        return uri;
    }

    private void sendConnected(final Channel channel) {
        if (connected != null) {
            MessageContent messageContent = this.connected.readFor(null);
            ByteBuf byteBuf = ByteBufs.toByteBuf(messageContent.getContent());
            channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
        }
    }

    public void connectRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getUri(), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        Channel channel = ctx.channel();
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
            return;
        }

        handshaker.handshake(channel, request);
        connect(channel);
        sendConnected(channel);
    }

    @Override
    protected Setting<WebsocketResponseSetting> newSetting(final RequestMatcher matcher) {
        return new WebsocketSetting(matcher);
    }

    @Override
    protected ActualWebSocketServer createMergeServer(final ActualWebSocketServer thatServer) {
        return new ActualWebSocketServer(this.uri);
    }

    @Override
    protected WebsocketResponseSetting onRequestAttached(final RequestMatcher matcher) {
        WebsocketSetting baseSetting = new WebsocketSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }

    public PongWebSocketFrame handlePingPong(final PingWebSocketFrame frame) {
        DefaultWebsocketRequest request = new DefaultWebsocketRequest(frame);
        DefaultWebsocketResponse response = new DefaultWebsocketResponse();
        SessionContext context = new SessionContext(request, response);
        for (PingPongSetting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(context);
                ByteBuf buf = ByteBufs.toByteBuf(context.getResponse().getContent().getContent());
                return new PongWebSocketFrame(buf);
            }
        }

        throw new IllegalArgumentException();
    }

    public WebsocketResponse handleRequest(final ChannelHandlerContext ctx, final WebSocketFrame message) {
        DefaultWebsocketRequest request = new DefaultWebsocketRequest(message);
        DefaultWebsocketResponse response = new DefaultWebsocketResponse();
        SessionContext context = new SessionContext(request, response);

        return (WebsocketResponse) this.getResponse(context).orElseThrow(IllegalArgumentException::new);
    }
}
