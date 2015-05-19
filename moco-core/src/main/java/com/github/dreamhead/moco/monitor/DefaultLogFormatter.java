package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultLogFormatter implements LogFormatter {
    private final Dumper<Request> requestDumper = new HttpRequestDumper();
    private final Dumper<Response> responseDumper = new HttpResponseDumper();

    @Override
    public String format(final Request request) {
        return String.format("Request received:\n\n%s\n", requestDumper.dump(request));
    }

    @Override
    public String format(final Response response) {
        return String.format("Response return:\n\n%s\n", responseDumper.dump(response));
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
}
