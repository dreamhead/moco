package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class CompositeRestSettings<T extends SimpleRestSetting> implements RestSetting {
    private final FluentIterable<T> settings;

    public CompositeRestSettings(final FluentIterable<T> settings) {
        this.settings = settings;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Optional<ResponseHandler> getMatched(final String name, final HttpRequest httpRequest) {
        for (SimpleRestSetting setting : settings) {
            Optional<ResponseHandler> responseHandler = setting.getMatched(name, httpRequest);
            if (responseHandler.isPresent()) {
                return responseHandler;
            }
        }

        return Optional.absent();
    }
}
