package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;

import java.util.Optional;

public final class ParamRequestExtractor extends HttpRequestExtractor<String[]> {
    private final String param;

    public ParamRequestExtractor(final String param) {
        this.param = param;
    }

    @Override
    protected Optional<String[]> doExtract(final HttpRequest request) {
        String[] reference = request.getQueries().get(this.param);
        return Optional.ofNullable(reference);
    }
}
