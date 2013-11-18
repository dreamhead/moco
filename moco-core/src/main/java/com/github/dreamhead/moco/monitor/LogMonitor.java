package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class LogMonitor implements MocoMonitor {
    private LogWriter writer;
    private LogFormatter formatter;

    public LogMonitor(LogFormatter formatter, LogWriter writer) {
        this.writer = writer;
        this.formatter = formatter;
    }

    @Override
    public void onMessageArrived(FullHttpRequest request) {
        writer.write(formatter.format(request));
    }

    @Override
    public void onException(Exception e) {
        writer.write(formatter.format(e));
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
        writer.write(formatter.format(response));
    }

    @Override
    public void onUnexpectedMessage(FullHttpRequest request) {
    }
}
