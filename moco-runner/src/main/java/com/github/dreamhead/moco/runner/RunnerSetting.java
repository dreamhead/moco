package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.parser.model.RequestSetting;
import com.github.dreamhead.moco.parser.model.ResponseSetting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class RunnerSetting {
    private final List<InputStream> streams;
    private final RequestSetting request;
    private final ResponseSetting response;
    private final String context;
    private final String fileRoot;

    private RunnerSetting(final List<InputStream> streams, final String context, final String fileRoot,
                          final RequestSetting requestSetting, final ResponseSetting response) {
        this.streams = streams;
        this.request = requestSetting;
        this.response = response;
        this.context = context;
        this.fileRoot = fileRoot;
    }

    public List<InputStream> getStreams() {
        return streams;
    }

    public Optional<MocoConfig> context() {
        if (context != null) {
            return of(Moco.context(context));
        }

        return empty();
    }

    public Optional<MocoConfig> fileRoot() {
        if (fileRoot != null) {
            return of(Moco.fileRoot(fileRoot));
        }

        return empty();
    }

    public Optional<MocoConfig> request() {
        if (request != null) {
            return of(Moco.request(request.getRequestMatcher()));
        }

        return empty();
    }

    public Optional<MocoConfig> response() {
        if (response != null) {
            return of(Moco.response(response.getResponseHandler()));
        }

        return empty();
    }

    public static Builder aRunnerSetting() {
        return new Builder();
    }

    public static class Builder {
        private final List<InputStream> streams = new ArrayList<>();
        private RequestSetting request;
        private ResponseSetting response;
        private String context;
        private String fileRoot;

        public final Builder addStream(final InputStream stream) {
            this.streams.add(stream);
            return this;
        }

        public final Builder addStreams(final List<InputStream> streams) {
            this.streams.addAll(streams);
            return this;
        }

        public final Builder withRequest(final RequestSetting request) {
            this.request = request;
            return this;
        }

        public final Builder withResponse(final ResponseSetting response) {
            this.response = response;
            return this;
        }

        public final Builder withContext(final String context) {
            this.context = context;
            return this;
        }

        public final Builder withFileRoot(final String fileRoot) {
            this.fileRoot = fileRoot;
            return this;
        }

        public final RunnerSetting build() {
            return new RunnerSetting(List.copyOf(streams), context, fileRoot, request, response);
        }
    }
}
