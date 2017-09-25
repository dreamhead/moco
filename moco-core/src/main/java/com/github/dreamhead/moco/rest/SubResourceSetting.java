package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

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
        for (RestSetting setting : settings) {
            RestIdMatcher idMatcher = RestIdMatchers.match(join(resourceName.resourceUri(),
                    this.id.resourceUri(),
                    this.name));
            Optional<ResponseHandler> responseHandler = setting.getMatched(idMatcher,
                    httpRequest);
            if (responseHandler.isPresent()) {
                return responseHandler;
            }
        }

        return Optional.absent();
    }
}
