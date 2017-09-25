package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

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
        for (RestSetting setting : settings) {
            Optional<ResponseHandler> responseHandler = setting.getMatched(resourceName, httpRequest);
            if (responseHandler.isPresent()) {
                return responseHandler;
            }
        }

        return Optional.absent();
    }

    public Iterable<T> getSettings() {
        return settings;
    }
}
