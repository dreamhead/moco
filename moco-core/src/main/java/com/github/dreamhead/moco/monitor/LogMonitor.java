package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class LogMonitor extends AbstractMonitor {
    private final LogWriter writer;
    private final LogFormatter formatter;

    public LogMonitor(final LogFormatter formatter, final LogWriter writer) {
        this.writer = writer;
        this.formatter = formatter;
    }

    @Override
    public void onMessageArrived(final HttpRequest request) {
        writer.write(formatter.format(request));
    }

    @Override
    public void onException(final Exception e) {
        writer.write(formatter.format(e));
    }

    @Override
    public void onMessageLeave(final FullHttpResponse response) {
        writer.write(formatter.format(response));
    }
}
