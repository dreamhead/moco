package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.HttpRequestFailoverMatcher;
import com.github.dreamhead.moco.model.Session;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dreamhead.moco.util.Jsons.writeValue;
import static com.google.common.collect.ImmutableList.of;

public final class DefaultFailoverExecutor implements FailoverExecutor {
    private static Logger logger = LoggerFactory.getLogger(DefaultFailoverExecutor.class);

    private final File file;

    public DefaultFailoverExecutor(final File file) {
        this.file = file;
    }

    @Override
    public void onCompleteResponse(final HttpRequest request, final HttpResponse response) {
        Session targetSession = Session.newSession(request, response);
        writeValue(this.file, prepareTargetSessions(this.file, targetSession));
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
        Optional<Session> session = sessions.stream()
                .filter(isForRequest(targetSession.getRequest()))
                .findFirst();
        if (session.isPresent()) {
            return sessions.stream()
                    .filter(isForRequest(targetSession.getRequest()).negate())
                    .collect(Collectors.toList());
        }

        return sessions;
    }

    private ImmutableList<Session> restoreSessions(final File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            return Jsons.toObjects(inputStream, Session.class);
        } catch (MocoException me) {
            logger.warn("exception found", me);
            return of();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @Override
    public HttpResponse failover(final HttpRequest request) {
        ImmutableList<Session> sessions = restoreSessions(this.file);
        final Optional<Session> session = sessions.stream().filter(isForRequest(request)).findFirst();
        if (session.isPresent()) {
            return session.get().getResponse();
        }

        logger.warn("No match request found: {}", request);
        throw new MocoException("no failover response found");
    }

    private Predicate<Session> isForRequest(final HttpRequest dumpedRequest) {
        return session -> {
            HttpRequest request = session.getRequest();
            return new HttpRequestFailoverMatcher(request).match(dumpedRequest);
        };
    }
}
