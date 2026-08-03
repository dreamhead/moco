package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.HttpRequestFailoverMatcher;
import com.github.dreamhead.moco.model.Session;
import com.github.dreamhead.moco.util.Jsons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.List.of;

public final class DefaultFailoverExecutor implements FailoverExecutor {
    private static Logger logger = LoggerFactory.getLogger(DefaultFailoverExecutor.class);

    private final File file;

    public DefaultFailoverExecutor(final File file) {
        this.file = file;
    }

    @Override
    public void onCompleteResponse(final HttpRequest request, final HttpResponse response) {
        Session targetSession = Session.newSession(request, response);
        Jsons.writeToFile(this.file, prepareTargetSessions(this.file, targetSession));
    }

    private List<Session> prepareTargetSessions(final File file, final Session targetSession) {
        if (file.length() == 0) {
            return of(targetSession);
        }

        return Stream.concat(
                        StreamSupport.stream(
                                toUniqueSessions(targetSession, restoreSessions(file)).spliterator(), false),
                        Stream.of(targetSession))
                .toList();
    }

    private Iterable<Session> toUniqueSessions(final Session targetSession, final List<Session> sessions) {
        Optional<Session> session = sessions.stream()
                .filter(isForRequest(targetSession.getRequest()))
                .findFirst();
        if (session.isPresent()) {
            return sessions.stream()
                    .filter(isForRequest(targetSession.getRequest()).negate())
                    .toList();
        }

        return sessions;
    }

    private List<Session> restoreSessions(final File file) {
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
        List<Session> sessions = restoreSessions(this.file);
        final Optional<Session> session = sessions.stream().filter(isForRequest(request)).findFirst();

        return session.map(Session::getResponse).orElseThrow(() -> {
            logger.warn("No match request found: {}", request);
            return new MocoException("no failover response found");
        });
    }

    private Predicate<Session> isForRequest(final HttpRequest dumpedRequest) {
        return session -> {
            HttpRequest request = session.getRequest();
            return new HttpRequestFailoverMatcher(request).match(dumpedRequest);
        };
    }
}
