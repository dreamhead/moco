package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static java.lang.String.format;

public abstract class OutputMonitor implements MocoMonitor {
    protected abstract void log(String content);

    private final Dumper<FullHttpRequest> requestDumper = new HttpRequestDumper();
    private final Dumper<FullHttpResponse> responseDumper = new HttpResponseDumper();

    @Override
    public void onMessageArrived(FullHttpRequest request) {
        log("Request received:\n\n%s\n", requestDumper.dump(request));
    }

    @Override
    public void onException(Exception e) {
        log("Exception thrown:\n\n%s\n", stackTraceToString(e));
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
        log("Response return:\n\n%s\n", responseDumper.dump(response));
    }

    @Override
    public void onUnexpectedMessage(FullHttpRequest request) {
    }

    private void log(String prompt, String content) {
        log(format(prompt, content));
    }

    private String stackTraceToString(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
