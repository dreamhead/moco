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

    public static MountPredicate include(final String glob) {
        checkNotNullOrEmpty(glob, "Glob should not be null or empty");
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);

        return new MountPredicate() {
            @Override
            public boolean apply(final String filename) {
                return matcher.matches(Paths.get(filename));
            }
        };
    }

    public static MountPredicate exclude(final String glob) {
        return not(include(checkNotNullOrEmpty(glob, "Glob should not be null")));
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
