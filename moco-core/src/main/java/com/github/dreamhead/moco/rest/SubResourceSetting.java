package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.util.URLs.join;

public class SubResourceSetting implements RestSetting {
    private final String id;
    private final String name;
    private final RestSetting[] settings;

    public SubResourceSetting(final String id,
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
    public Optional<ResponseHandler> getMatched(final String name, final HttpRequest httpRequest) {
        for (RestSetting setting : settings) {
            Optional<ResponseHandler> responseHandler = setting.getMatched(join(name, this.id, this.name),
                    httpRequest);
            if (responseHandler.isPresent()) {
                return responseHandler;
            }
        }

        return Optional.absent();
    }
}
