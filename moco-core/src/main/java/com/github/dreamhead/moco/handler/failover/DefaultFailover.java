package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.model.DefaultRequest;
import com.github.dreamhead.moco.model.MessageFactory;
import com.github.dreamhead.moco.model.Response;
import com.github.dreamhead.moco.model.Session;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.tryFind;

public class DefaultFailover implements Failover {
    private static final Logger logger = LoggerFactory.getLogger(DefaultFailover.class);
    private final TypeFactory factory = TypeFactory.defaultInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public DefaultFailover(File file) {
        this.file = file;
    }

    public void onCompleteResponse(FullHttpRequest request, FullHttpResponse response) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Session targetSession = Session.newSession(MessageFactory.createRequest(request), MessageFactory.createResponse(response));
            writer.writeValue(this.file, prepareTargetSessions(targetSession));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ImmutableList<Session> prepareTargetSessions(Session targetSession) {
        ImmutableList<Session> sessions = restoreSessions(this.file);
        Optional<Session> session = tryFind(sessions, isForRequest(targetSession.getRequest()));
        if (session.isPresent()) {
            session.get().setResponse(targetSession.getResponse());
            return sessions;
        }

        ImmutableList.Builder<Session> builder = ImmutableList.builder();
        return builder.addAll(sessions).add(targetSession).build();
    }

    private ImmutableList<Session> restoreSessions(File file) {
        try {
            List<Session> sessions = mapper.readValue(file, factory.constructCollectionType(List.class, Session.class));
            return copyOf(sessions);
        } catch (JsonMappingException jme) {
            logger.error("exception found", jme);
            return of();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void failover(FullHttpRequest request, FullHttpResponse response) {
        Response dumpedResponse = failoverResponse(request);
        response.setProtocolVersion(HttpVersion.valueOf(dumpedResponse.getVersion()));
        response.setStatus(HttpResponseStatus.valueOf(dumpedResponse.getStatusCode()));
        for (Map.Entry<String, String> entry : dumpedResponse.getHeaders().entrySet()) {
            response.headers().add(entry.getKey(), entry.getValue());
        }

        response.content().writeBytes(dumpedResponse.getContent().getBytes());
    }

    private Response failoverResponse(FullHttpRequest request) {
        final DefaultRequest dumpedRequest = MessageFactory.createRequest(request);
        ImmutableList<Session> sessions = restoreSessions(this.file);
        final Optional<Session> session = tryFind(sessions, isForRequest(dumpedRequest));
        if (session.isPresent()) {
            return session.get().getResponse();
        }

        logger.error("No match request found: {}", dumpedRequest);
        throw new RuntimeException("no failover response found");
    }

    private Predicate<Session> isForRequest(final DefaultRequest dumpedRequest) {
        return new Predicate<Session>() {
            @Override
            public boolean apply(Session session) {
                return session.getRequest().match(dumpedRequest);
            }
        };
    }
}
