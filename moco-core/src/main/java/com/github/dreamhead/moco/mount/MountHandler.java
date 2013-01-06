package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;

public class MountHandler implements ResponseHandler {
    private UriRequestExtractor extractor = new UriRequestExtractor();

    private File dir;
    private MountTo target;

    public MountHandler(File dir, MountTo target) {
        this.dir = dir;
        this.target = target;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        writeResponse(request, buffer);
        response.setContent(buffer);
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setHeader("Content-Length", response.getContent().writerIndex());
    }

    private void writeResponse(HttpRequest request, ChannelBuffer buffer) {
        try {
            buffer.writeBytes(toByteArray(targetFile(request)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File targetFile(HttpRequest request) {
        String relativePath = target.extract(extractor.extract(request));
        return new File(dir, relativePath);
    }
}
