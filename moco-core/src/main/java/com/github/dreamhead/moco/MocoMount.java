package com.github.dreamhead.moco;

import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoMount {
    public static MountTo to(final String target) {
        return new MountTo(checkNotNullOrEmpty(target, "Target should not be null"));
    }

    public static MountPredicate include(final String wildcard) {
        checkNotNullOrEmpty(wildcard, "Wildcard should not be null");
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);

        return new MountPredicate() {
            @Override
            public boolean apply(final String filename) {
                return matcher.matches(Paths.get(filename));
            }
        };
    }

    public static MountPredicate exclude(final String wildcard) {
        return not(include(checkNotNullOrEmpty(wildcard, "Wildcard should not be null")));
    }

    private static MountPredicate not(final MountPredicate predicate) {
        checkNotNull(predicate);
        return new MountPredicate() {
            @Override
            public boolean apply(final String filename) {
                return !predicate.apply(filename);
            }
        };
    }

    private MocoMount() {
    }
}
