package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Strings.isNullOrEmpty;

public class MountMatcher implements RequestMatcher {
    private final MountPathExtractor extractor;

    private final File dir;
    private final Iterable<MountPredicate> predicates;

    public MountMatcher(File dir, MountTo target, Iterable<MountPredicate> predicates) {
        this.dir = dir;
        this.predicates = predicates;
        this.extractor = new MountPathExtractor(target);
    }

    @Override
    public boolean match(HttpRequest request) {
        String relativePath = extractor.extract(request);
        return isTarget(relativePath) && new File(dir, relativePath).exists();
    }

    private boolean isTarget(String relativePath) {
        return !isNullOrEmpty(relativePath) && and(predicates).apply(relativePath);
    }
}
