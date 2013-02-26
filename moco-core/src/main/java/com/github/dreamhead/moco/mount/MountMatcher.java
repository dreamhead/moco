package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Strings.isNullOrEmpty;

public class MountMatcher implements RequestMatcher {
    private final UriRequestExtractor extractor = new UriRequestExtractor();

    private final File dir;
    private final MountTo target;
    private final Iterable<MountPredicate> predicates;

    public MountMatcher(File dir, MountTo target, Iterable<MountPredicate> predicates) {
        this.dir = dir;
        this.target = target;
        this.predicates = predicates;
    }

    @Override
    public boolean match(HttpRequest request) {
        String relativePath = target.extract(extractor.extract(request));
        return isTarget(relativePath) && new File(dir, relativePath).exists();

    }

    private boolean isTarget(String relativePath) {
        return !isNullOrEmpty(relativePath) && and(predicates).apply(relativePath);
    }
}
