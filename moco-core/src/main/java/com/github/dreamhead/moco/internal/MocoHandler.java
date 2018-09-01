package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.collect.ImmutableList;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.model.DefaultMutableHttpResponse.newResponse;
import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpUtil.isContentLengthSet;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpUtil.setKeepAlive;

@Sharable
public final class MocoHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final int DEFAULT_STATUS = HttpResponseStatus.OK.code();
    private final ImmutableList<Setting<HttpResponseSetting>> settings;
    private final Setting<HttpResponseSetting> anySetting;
    private final MocoMonitor monitor;

    public MocoHandler(final ActualHttpServer server) {
        this.settings = server.getSettings();
        this.anySetting = server.getAnySetting();
        this.monitor = server.getMonitor();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest message) {
        FullHttpResponse response = handleRequest(message);
        closeIfNotKeepAlive(message, ctx.write(response));
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse handleRequest(final FullHttpRequest message) {
        HttpRequest request = DefaultHttpRequest.newRequest(message);
        DefaultMutableHttpResponse httpResponse = getHttpResponse(request);
        FullHttpResponse response = httpResponse.toFullResponse();
        prepareForKeepAlive(message, response);
        monitor.onMessageLeave(httpResponse);
        return response;
    }

    private DefaultMutableHttpResponse getHttpResponse(final HttpRequest request) {
        try {
            monitor.onMessageArrived(request);
            return doGetHttpResponse(request);
        } catch (RuntimeException e) {
            monitor.onException(e);
            return newResponse(request, HttpResponseStatus.BAD_REQUEST.code());
        } catch (Exception e) {
            monitor.onException(e);
            return newResponse(request, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
    }

    private DefaultMutableHttpResponse doGetHttpResponse(final HttpRequest request) {
        DefaultMutableHttpResponse httpResponse = newResponse(request, DEFAULT_STATUS);
        SessionContext context = new SessionContext(request, httpResponse);

        for (Setting setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(context);
                return httpResponse;
            }
        }

        if (anySetting.match(request)) {
            anySetting.writeToResponse(context);
            return httpResponse;
        }

        monitor.onUnexpectedMessage(request);
        return newResponse(request, HttpResponseStatus.BAD_REQUEST.code());
    }

    private void closeIfNotKeepAlive(final FullHttpRequest request, final ChannelFuture future) {
        if (!isKeepAlive(request)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void prepareForKeepAlive(final FullHttpRequest request, final FullHttpResponse response) {
        if (isKeepAlive(request)) {
            setKeepAlive(response, true);
            setContentLengthForKeepAlive(response);
        }
    }

    private void setContentLengthForKeepAlive(final FullHttpResponse response) {
        if (!isContentLengthSet(response)) {
            setContentLength(response, response.content().writerIndex());
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        monitor.onException(cause);
    }
}
