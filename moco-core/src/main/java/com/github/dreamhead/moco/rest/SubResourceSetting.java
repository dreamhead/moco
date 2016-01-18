package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.util.URLs.join;

public class SubResourceSetting implements RestSetting {
    private final RestIdMatcher id;
    private final String name;
    private final RestSetting[] settings;

    public SubResourceSetting(final RestIdMatcher id,
                              final String name,
                              final RestSetting[] settings) {
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
            RestIdMatcher idMatcher = RestIdMatchers.match(join(resourceName.resourceUri(Optional.<RestIdMatcher>absent()),
                    this.id.resourceUri(Optional.<RestIdMatcher>absent()),
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
