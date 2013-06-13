package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class DefaultFailover implements Failover {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public DefaultFailover(File file) {
        this.file = file;
    }

    public void onCompleteResponse(HttpRequest request, HttpResponse response) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(this.file, createDumpedResponse(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void failover(HttpResponse response) {
        try {
            Response dumpedResponse = mapper.readValue(this.file, Response.class);
            response.setProtocolVersion(HttpVersion.valueOf(dumpedResponse.getVersion()));
            response.setStatus(HttpResponseStatus.valueOf(dumpedResponse.getStatusCode()));
            for (Map.Entry<String, String> entry : dumpedResponse.getHeaders().entrySet()) {
                response.addHeader(entry.getKey(), entry.getValue());
            }

            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            buffer.writeBytes(dumpedResponse.getContent().getBytes());
            response.setContent(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response createDumpedResponse(HttpResponse response) {
        Response dumpedResponse = new Response();
        dumpedResponse.setStatusCode(response.getStatus().getCode());
        dumpedResponse.setVersion(response.getProtocolVersion().getText());
        for (Map.Entry<String, String> entry : response.getHeaders()) {
            dumpedResponse.addHeader(entry.getKey(), entry.getValue());
        }
        dumpedResponse.setContent(response.getContent().toString(Charset.defaultCharset()));
        return dumpedResponse;
    }
}
