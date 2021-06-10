package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;

import java.util.Optional;
import java.util.stream.StreamSupport;

public final class CompositeRestSetting<T extends SimpleRestSetting> implements RestSetting {
    private final Iterable<T> settings;

    public CompositeRestSetting(final Iterable<T> settings) {
        this.settings = settings;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Optional<ResponseHandler> getMatched(final RestIdMatcher resourceName, final HttpRequest httpRequest) {
        return StreamSupport.stream(settings.spliterator(), false)
                .map(settings -> settings.getMatched(resourceName, httpRequest))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public Iterable<T> getSettings() {
        return settings;
    }
}
