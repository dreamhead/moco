package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultLogFormatter implements LogFormatter {
    private final Dumper<FullHttpRequest> requestDumper = new HttpRequestDumper();
    private final Dumper<FullHttpResponse> responseDumper = new HttpResponseDumper();

    @Override
    public String format(FullHttpRequest request) {
        return String.format("Request received:\n\n%s\n", requestDumper.dump(request));
    }

    @Override
    public String format(FullHttpResponse response) {
        return String.format("Response return:\n\n%s\n", responseDumper.dump(response));
    }

    @Override
    public String format(Exception e) {
        return String.format("Exception thrown:\n\n%s\n", stackTraceToString(e));
    }

    private String stackTraceToString(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
