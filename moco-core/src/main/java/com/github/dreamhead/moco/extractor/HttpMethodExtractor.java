package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public final class HttpMethodExtractor extends HttpRequestExtractor<String> {
    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        return of(request.getMethod().name());
    }
}
