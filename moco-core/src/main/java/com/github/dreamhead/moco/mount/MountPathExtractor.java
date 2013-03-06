package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class MountPathExtractor {
    private final MountTo target;
    private final UriRequestExtractor extractor = new UriRequestExtractor();

    public MountPathExtractor(MountTo target) {
        this.target = target;
    }

    public String extract(HttpRequest request) {
        return target.extract(extractor.extract(request));
    }
}
