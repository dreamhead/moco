package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public class MountPathExtractor implements RequestExtractor<String> {
    private final MountTo target;
    private final RequestExtractor<String> extractor = new UriRequestExtractor();

    public MountPathExtractor(final MountTo target) {
        this.target = target;
    }

    public Optional<String> extract(final HttpRequest request) {
        return of(target.extract(extractor.extract(request).get()));
    }
}
