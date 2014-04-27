package com.github.dreamhead.moco;

import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;

import static org.apache.commons.io.FilenameUtils.wildcardMatch;

public class MocoMount {
    public static MountTo to(final String target) {
        return new MountTo(target);
    }

    public static MountPredicate include(final String wildcard) {
        return new MountPredicate() {
            @Override
            public boolean apply(String filename) {
                return wildcardMatch(filename, wildcard);
            }
        };
    }

    public static MountPredicate exclude(final String wildcard) {
        return not(include(wildcard));
    }

    private static MountPredicate not(final MountPredicate predicate) {
        return new MountPredicate() {
            @Override
            public boolean apply(String filename) {
                return !predicate.apply(filename);
            }
        };
    }
}
