package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.Response;
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
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ActualWebSocketServer
        extends BaseActualServer<WebsocketResponseSetting, ActualWebSocketServer>
        implements WebSocketServer {
    private Resource connected;
    private ChannelSessionGroup group;
    private final String uri;
    private List<PingPongSetting> settings;

    public ActualWebSocketServer(final String uri) {
        super(0, new QuietMonitor(), new MocoConfig[0]);
        this.uri = uri;
        this.group = new ChannelSessionGroup(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        this.settings = new ArrayList<>();
    }

    public void connected(final Resource resource) {
        checkArgument(this.connected == null, "Only one connected can be setup");
        this.connected = checkNotNull(resource, "Connected resource should not be null");
    }

    public void connected(final String text) {
        checkArgument(this.connected == null, "Only one connected can be setup");
        this.connected(text(checkNotNull(text, "Connected text should not be null")));
    }

    @Override
    public PongResponse ping(final String message) {
        return this.ping(text(checkNotNullOrEmpty(message, "Ping message should not be null")));
    }

    @Override
    public PongResponse ping(final Resource message) {
        Resource resource = checkNotNull(message, "Ping message should not be null");
        return ping(by(resource));
    }

    @Override
    public PongResponse ping(final RequestMatcher matcher) {
        RequestMatcher actual = checkNotNull(matcher, "Ping message should not be null");
        PingPongSetting setting = new PingPongSetting(actual);
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
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        final String actual = decoder.path();
        if (!uri.equals(actual)) {
            ctx.writeAndFlush(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                this.uri, null, false);
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
    protected void addExtension(final ActualWebSocketServer server) {
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
        Response result = this.getPongResponse(context).orElseThrow(IllegalArgumentException::new);
        ByteBuf buf = ByteBufs.toByteBuf(result.getContent().getContent());
        return new PongWebSocketFrame(buf);
    }

    private Optional<Response> getPongResponse(final SessionContext context) {
        Request request = context.getRequest();
        final Optional<PingPongSetting> actual = settings.stream()
                .filter(setting -> setting.match(request))
                .findFirst();

        actual.ifPresent(setting -> setting.writeToResponse(context));
        return actual.flatMap(setting -> Optional.of(context.getResponse()));
    }

    public Optional<WebsocketResponse> handleRequest(final ChannelHandlerContext ctx, final WebSocketFrame message) {
        DefaultWebsocketRequest request = new DefaultWebsocketRequest(message);
        DefaultWebsocketResponse response = new DefaultWebsocketResponse();
        SessionContext context = new SessionContext(request, response,
                new ContextSessionGroup(this.group, ctx.channel()));

        return this.getResponse(context).flatMap(this::asWebsocketResponse);
    }

    private Optional<WebsocketResponse> asWebsocketResponse(final Response response) {
        if (response.getContent() != null) {
            return Optional.of((WebsocketResponse) response);
        }

        return Optional.empty();
    }
}
