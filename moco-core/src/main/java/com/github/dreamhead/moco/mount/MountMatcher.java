package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;

public class MountMatcher implements RequestMatcher {
    private UriRequestExtractor extractor = new UriRequestExtractor();

    private String dir;
    private MountTo target;

    public MountMatcher(String dir, MountTo target) {
        this.dir = dir;
        this.target = target;
    }

    @Override
    public boolean match(HttpRequest request) {
        String relativePath = target.extract(extractor.extract(request));
        return relativePath != null && new File(dir, relativePath).exists();
    }
}
