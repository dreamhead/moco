package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.SocketRequest;
import com.github.dreamhead.moco.SocketResponse;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.dumper.SocketRequestDumper;
import com.github.dreamhead.moco.dumper.SocketResponseDumper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultLogFormatter implements LogFormatter {
    @Override
    public String format(final Request request) {
        return String.format("Request received:\n\n%s\n", requestDumper(request).dump(request));
    }

    @Override
    public String format(final Response response) {
        return String.format("Response return:\n\n%s\n", responseDumper(response).dump(response));
    }

    @Override
    public String format(final Throwable e) {
        return String.format("Exception thrown:\n\n%s\n", stackTraceToString(e));
    }

    private String stackTraceToString(final Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private Dumper<Request> requestDumper(final Request request) {
        if (request instanceof HttpRequest) {
            return new HttpRequestDumper();
        }

        if (request instanceof SocketRequest) {
            return new SocketRequestDumper();
        }

        throw new IllegalArgumentException("Unknown request type:" + request.getClass());
    }

    private Dumper<Response> responseDumper(final Response response) {
        if (response instanceof HttpResponse) {
            return new HttpResponseDumper();
        }

        if (response instanceof SocketResponse) {
            return new SocketResponseDumper();
        }

        throw new IllegalArgumentException("Unknown response type:" + response.getClass());
    }
}
