package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.HttpRequestFailoverMatcher;
import com.github.dreamhead.moco.model.Session;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.tryFind;

public final class DefaultFailoverExecutor implements FailoverExecutor {
    private static Logger logger = LoggerFactory.getLogger(DefaultFailoverExecutor.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public DefaultFailoverExecutor(final File file) {
        this.file = file;
    }

    @Override
    public void onCompleteResponse(final HttpRequest request, final HttpResponse response) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Session targetSession = Session.newSession(request, response);
            writer.writeValue(this.file, prepareTargetSessions(this.file, targetSession));
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private ImmutableList<Session> prepareTargetSessions(final File file, final Session targetSession) {
        if (file.length() == 0) {
            return of(targetSession);
        }

        return ImmutableList.<Session>builder()
                .addAll(toUniqueSessions(targetSession, restoreSessions(file)))
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
            InputStream inputStream = new FileInputStream(file);
            return Jsons.toObjects(inputStream, Session.class);
        } catch (MocoException me) {
            logger.error("exception found", me);
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
