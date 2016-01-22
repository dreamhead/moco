package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.HttpRequestFailoverMatcher;
import com.github.dreamhead.moco.model.Session;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.tryFind;

public class DefaultFailoverExecutor implements FailoverExecutor {
    private static Logger logger = LoggerFactory.getLogger(DefaultFailoverExecutor.class);

    private final TypeFactory factory = TypeFactory.defaultInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public DefaultFailoverExecutor(final File file) {
        this.file = file;
    }

    @Override
    public void onCompleteResponse(final HttpRequest request, final HttpResponse httpResponse) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Session targetSession = Session.newSession(request, httpResponse);
            writer.writeValue(this.file, prepareTargetSessions(targetSession));
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private ImmutableList<Session> prepareTargetSessions(final Session targetSession) {
        if (file.length() == 0) {
            return of(targetSession);
        }

        return ImmutableList.<Session>builder()
                .addAll(toUniqueSessions(targetSession, restoreSessions(this.file)))
                .add(targetSession)
                .build();
    }

    private Iterable<Session> toUniqueSessions(final Session targetSession, final ImmutableList<Session> sessions) {
        Optional<Session> session = tryFind(sessions, isForRequest(targetSession.getRequest()));
        if (session.isPresent()) {
            return from(sessions).filter(not(isForRequest(targetSession.getRequest())));
        }

        return sessions;
    }

    private ImmutableList<Session> restoreSessions(final File file) {
        try {
            List<Session> sessions = mapper.readValue(file, factory.constructCollectionType(List.class, Session.class));
            return copyOf(sessions);
        } catch (JsonMappingException jme) {
            logger.error("exception found", jme);
            return of();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @Override
    public HttpResponse failover(final HttpRequest request) {
        ImmutableList<Session> sessions = restoreSessions(this.file);
        final Optional<Session> session = tryFind(sessions, isForRequest(request));
        if (session.isPresent()) {
            return session.get().getResponse();
        }

        logger.error("No match request found: {}", request);
        throw new MocoException("no failover response found");
    }

    private Predicate<Session> isForRequest(final HttpRequest dumpedRequest) {
        return new Predicate<Session>() {
            @Override
            public boolean apply(final Session session) {
                HttpRequest request = session.getRequest();
                return new HttpRequestFailoverMatcher(request).match(dumpedRequest);
            }
        };
    }
}
