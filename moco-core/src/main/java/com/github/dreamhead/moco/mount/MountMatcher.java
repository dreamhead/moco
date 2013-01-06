package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import com.google.common.collect.ImmutableList;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;

import static com.google.common.base.Predicates.and;

public class MountMatcher implements RequestMatcher {
    private UriRequestExtractor extractor = new UriRequestExtractor();

    private File dir;
    private MountTo target;
    private ImmutableList<MountPredicate> predicates;

    public MountMatcher(File dir, MountTo target, ImmutableList<MountPredicate> predicates) {
        this.dir = dir;
        this.target = target;
        this.predicates = predicates;
    }

    @Override
    public boolean match(HttpRequest request) {
        String relativePath = target.extract(extractor.extract(request));
        if (relativePath == null || !and(predicates).apply(relativePath)) {
            return false;
        }

        return new File(dir, relativePath).exists();
    }
}
