package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.fromNullable;

public class ParamRequestExtractor extends HttpRequestExtractor<String> {
    private final String param;

    public ParamRequestExtractor(final String param) {
        this.param = param;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        return fromNullable(request.getQueries().get(this.param));
    }
}
