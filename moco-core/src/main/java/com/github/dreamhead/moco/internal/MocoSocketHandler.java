package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.SocketRequest;
import com.github.dreamhead.moco.model.DefaultSocketRequest;
import com.github.dreamhead.moco.model.DefaultSocketResponse;
import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Optional;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static io.netty.channel.ChannelHandler.Sharable;
import static java.lang.String.format;

@Sharable
public final class MocoSocketHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final ActualSocketServer server;

    public MocoSocketHandler(final ActualSocketServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) throws Exception {
        MessageContent content = content().withContent(new ByteBufInputStream(msg)).build();
        SocketRequest request = new DefaultSocketRequest(content);
        SessionContext context = new SessionContext(request, new DefaultSocketResponse());
        Optional<Response> response = server.getResponse(context);
        Response actual = response.orElseThrow(() ->
                new MocoException(format("No handler found for request: %s", context.getRequest().getContent())));
        ctx.write(Unpooled.wrappedBuffer(actual.getContent().getContent()));
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        this.server.onException(cause);
    }
}
