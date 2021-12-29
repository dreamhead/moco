package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.collect.Streams;

import java.util.Optional;

import static com.github.dreamhead.moco.util.URLs.join;

public final class SubResourceSetting implements RestSetting {
    private final RestIdMatcher id;
    private final String name;
    private final Iterable<RestSetting> settings;

    public SubResourceSetting(final RestIdMatcher id,
                              final String name,
                              final Iterable<RestSetting> settings) {
        this.id = id;
        this.name = name;
        this.settings = settings;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Optional<ResponseHandler> getMatched(final RestIdMatcher resourceName, final HttpRequest httpRequest) {
        return Streams.stream(settings)
                .map(setting -> getResponseHandler(resourceName, httpRequest, setting))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private Optional<ResponseHandler> getResponseHandler(final RestIdMatcher resourceName,
                                                         final HttpRequest httpRequest,
                                                         final RestSetting setting) {
        RestIdMatcher idMatcher = RestIdMatchers.match(join(resourceName.resourceUri(),
                this.id.resourceUri(),
                this.name));
        return setting.getMatched(idMatcher, httpRequest);
    }
}
