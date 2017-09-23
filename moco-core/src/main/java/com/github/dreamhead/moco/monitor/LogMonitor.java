package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public final class LogMonitor extends AbstractMonitor {
    private final LogWriter writer;
    private final LogFormatter formatter;

    public LogMonitor(final LogFormatter formatter, final LogWriter writer) {
        this.writer = writer;
        this.formatter = formatter;
    }

    @Override
    public void onMessageArrived(final Request request) {
        writer.write(formatter.format(request));
    }

    @Override
    public void onException(final Throwable t) {
        writer.write(formatter.format(t));
    }

    @Override
    public void onMessageLeave(final Response response) {
        writer.write(formatter.format(response));
    }
}
