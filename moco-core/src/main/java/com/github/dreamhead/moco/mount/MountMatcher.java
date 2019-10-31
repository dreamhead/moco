package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.matcher.AbstractRequestMatcher;

import java.io.File;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class MountMatcher extends AbstractRequestMatcher {
    private final MountPathExtractor extractor;

    private final File dir;
    private final Iterable<MountPredicate> predicates;
    private final MountTo target;

    public MountMatcher(final File dir, final MountTo target, final Iterable<MountPredicate> predicates) {
        this.dir = dir;
        this.predicates = predicates;
        this.target = target;
        this.extractor = new MountPathExtractor(target);
    }

    @Override
    public boolean match(final Request request) {
        Optional<String> optionalPath = extractor.extract(request);
        if (optionalPath.isPresent()) {
            String relativePath = optionalPath.get();
            return isTarget(relativePath) && new File(dir, relativePath).exists();
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RequestMatcher doApply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            return new MountMatcher(this.dir, this.target.apply(config), this.predicates);
        }

        if (config.isFor(MocoConfig.FILE_ID)) {
            return new MountMatcher(new File((String) config.apply(this.dir.getName())), this.target, this.predicates);
        }

        return this;
    }

    private boolean isTarget(final String relativePath) {
        return !isNullOrEmpty(relativePath) &&
                StreamSupport.stream(predicates.spliterator(), false)
                        .allMatch(mountPredicate -> mountPredicate.test(relativePath));
    }

}
