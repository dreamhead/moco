package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import io.netty.handler.codec.http.FullHttpRequest;

public class MountPathExtractor {
    private final MountTo target;
    private final UriRequestExtractor extractor = new UriRequestExtractor();

    public MountPathExtractor(MountTo target) {
        this.target = target;
    }

    public String extract(FullHttpRequest request) {
        return target.extract(extractor.extract(request).get());
    }
}
