package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.fromNullable;

public class ParamRequestExtractor implements RequestExtractor<String> {
    private final String param;

    public ParamRequestExtractor(final String param) {
        this.param = param;
    }

    @Override
    public Optional<String> extract(final HttpRequest request) {
        return fromNullable(request.getQueries().get(this.param));
    }
}
