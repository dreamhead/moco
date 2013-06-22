package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultFailover implements Failover {
    private static final Logger logger = LoggerFactory.getLogger(DefaultFailover.class);
    private final TypeFactory factory = TypeFactory.defaultInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public DefaultFailover(File file) {
        this.file = file;
    }

    public void onCompleteResponse(HttpRequest request, HttpResponse response) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Session targetSession = Session.newSession(createDumpedRequest(request), createDumpedResponse(response));
            writer.writeValue(this.file, prepareTargetSessions(targetSession));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Session> prepareTargetSessions(Session targetSession) {
        List<Session> sessions = restoreSessions(this.file);
        Optional<Session> session = tryFind(sessions, isForRequest(targetSession.getRequest()));
        if (session.isPresent()) {
            session.get().setResponse(targetSession.getResponse());
        } else {
            sessions.add(targetSession);
        }
        return sessions;
    }

    private List<Session> restoreSessions(File file) {
        try {
            return mapper.readValue(file, factory.constructCollectionType(List.class, Session.class));
        } catch (JsonMappingException jme) {
            logger.error("exception found", jme);
            return newArrayList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void failover(HttpRequest request, HttpResponse response) {
        Response dumpedResponse = failoverResponse(request);
        response.setProtocolVersion(HttpVersion.valueOf(dumpedResponse.getVersion()));
        response.setStatus(HttpResponseStatus.valueOf(dumpedResponse.getStatusCode()));
        for (Map.Entry<String, String> entry : dumpedResponse.getHeaders().entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes(dumpedResponse.getContent().getBytes());
        response.setContent(buffer);
    }

    private Response failoverResponse(HttpRequest request) {
        final Request dumpedRequest = createDumpedRequest(request);
        List<Session> sessions = restoreSessions(this.file);
        logger.info("Restore session size: {}", sessions.size());
        final Optional<Session> session = tryFind(sessions, isForRequest(dumpedRequest));
        if (session.isPresent()) {
            return session.get().getResponse();
        }

        logger.error("No match request found: {}", dumpedRequest);
        throw new RuntimeException("no failover response found");
    }

    private Predicate<Session> isForRequest(final Request dumpedRequest) {
        return new Predicate<Session>() {
            @Override
            public boolean apply(Session session) {
                logger.info("Failover request: {}", session.getRequest());
                return dumpedRequest.match(session.getRequest());
            }
        };
    }

    private Request createDumpedRequest(HttpRequest request) {
        Request dumpedRequest = new Request();
        dumpedRequest.setVersion(request.getProtocolVersion().getText());
        String content = request.getContent().toString(Charset.defaultCharset());
        if (!Strings.isNullOrEmpty(content)) {
            dumpedRequest.setContent(content);
        }
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
