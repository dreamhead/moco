package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.google.common.io.Files;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.*;
import java.nio.charset.Charset;

import static java.lang.String.format;

public class FileMonitor implements MocoMonitor {

    private final Dumper<FullHttpRequest> requestDumper = new HttpRequestDumper();
    private final Dumper<FullHttpResponse> responseDumper = new HttpResponseDumper();
    private final File file;

    public FileMonitor(String filename) {
        this.file = new File(filename);
    }

    @Override
    public void onMessageArrived(FullHttpRequest request) {
        log(format("Request received:\n\n%s\n", requestDumper.dump(request)));
    }

    @Override
    public void onException(Exception e) {
        log(format("Exception thrown:\n\n%s\n", stackTraceToString(e)));
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
        log(format("Response return:\n\n%s\n", responseDumper.dump(response)));
    }

    @Override
    public void onUnexpectedMessage(FullHttpRequest request) {
    }

    private void log(String content) {
        try {
            Files.append(content, file, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String stackTraceToString(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
