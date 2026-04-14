package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.model.DefaultMutableHttpResponse.newResponse;

public class HttpHandler {
    private static final int DEFAULT_STATUS = HttpResponseStatus.OK.code();

    private final ActualHttpServer server;

    public HttpHandler(final ActualHttpServer server) {
        this.server = server;
    }

    public final DefaultMutableHttpResponse handleRequest(final FullHttpRequest message, final Client client) {
        HttpRequest request = DefaultHttpRequest.newRequest(message, client);
        return getHttpResponse(request);
    }

    private DefaultMutableHttpResponse getHttpResponse(final HttpRequest request) {
        DefaultMutableHttpResponse httpResponse = newResponse(request, DEFAULT_STATUS);
        SessionContext context = new SessionContext(request, httpResponse);
        return doGetResponse(request, context);
    }

    private DefaultMutableHttpResponse doGetResponse(final HttpRequest request, final SessionContext context) {
        try {
            return (DefaultMutableHttpResponse) server.getResponse(context)
                    .orElse(newResponse(request, HttpResponseStatus.BAD_REQUEST.code()));
        } catch (RuntimeException e) {
            return newResponse(request, HttpResponseStatus.BAD_REQUEST.code());
        } catch (Exception e) {
            return newResponse(request, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
    }
}
