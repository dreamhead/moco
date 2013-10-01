package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;

import static com.google.common.base.Optional.of;

public class MountPathExtractor implements RequestExtractor {
    private final MountTo target;
    private final UriRequestExtractor extractor = new UriRequestExtractor();

    public MountPathExtractor(MountTo target) {
        this.target = target;
    }

    public Optional<String> extract(FullHttpRequest request) {
        return of(target.extract(extractor.extract(request).get()));
    }
}
