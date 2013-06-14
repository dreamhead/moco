package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class DefaultFailover implements Failover {
    private final TypeFactory factory = TypeFactory.defaultInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public DefaultFailover(File file) {
        this.file = file;
    }

    public void onCompleteResponse(HttpRequest request, HttpResponse response) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Session session = Session.newSession(createDumpedRequest(request), createDumpedResponse(response));
            List<Session> sessions = restoreSessions(this.file);
            sessions.add(session);
            writer.writeValue(this.file, sessions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Session> restoreSessions(File file) {
        try {
            return mapper.readValue(file, factory.constructCollectionType(List.class, Session.class));
        } catch (JsonMappingException jme) {
            return newArrayList();
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

    private Request createDumpedRequest(HttpRequest request) {
        Request dumpedRequest = new Request();
        dumpedRequest.setVersion(request.getProtocolVersion().getText());
        dumpedRequest.setContent(request.getContent().toString(Charset.defaultCharset()));
        dumpedRequest.setMethod(request.getMethod().getName());

        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        for (Map.Entry<String, List<String>> entry : decoder.getParameters().entrySet()) {
            dumpedRequest.addQuery(entry.getKey(), entry.getValue().get(0));
        }

        for (Map.Entry<String, String> entry : request.getHeaders()) {
            dumpedRequest.addHeader(entry.getKey(), entry.getValue());
        }

        return dumpedRequest;
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
