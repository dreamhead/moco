package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;

import java.util.Optional;

public final class MountPathExtractor extends HttpRequestExtractor<String> {
    private final MountTo target;
    private final RequestExtractor<String> extractor = new UriRequestExtractor();

    public MountPathExtractor(final MountTo target) {
        this.target = target;
    }

    @Override
    protected Optional<String> doExtract(final HttpRequest request) {
        Optional<String> extractedUri = extractor.extract(request);
        return extractedUri.flatMap(target::extract);
    }
}
