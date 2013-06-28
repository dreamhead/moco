package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Strings.isNullOrEmpty;

public class MountMatcher implements RequestMatcher {
    private final MountPathExtractor extractor;

    private final File dir;
    private final Iterable<MountPredicate> predicates;
    private final MountTo target;

    public MountMatcher(File dir, MountTo target, Iterable<MountPredicate> predicates) {
        this.dir = dir;
        this.predicates = predicates;
        this.target = target;
        this.extractor = new MountPathExtractor(target);
    }

    @Override
    public boolean match(HttpRequest request) {
        String relativePath = extractor.extract(request);
        return isTarget(relativePath) && new File(dir, relativePath).exists();
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        if (config.isFor("uri")) {
            return new MountMatcher(this.dir, this.target.apply(config), this.predicates);
        }

        if (config.isFor("file")) {
            return new MountMatcher(new File(config.apply(this.dir.getName())), this.target, this.predicates);
        }

        return this;
    }

    private boolean isTarget(String relativePath) {
        return !isNullOrEmpty(relativePath) && and(predicates).apply(relativePath);
    }
}
