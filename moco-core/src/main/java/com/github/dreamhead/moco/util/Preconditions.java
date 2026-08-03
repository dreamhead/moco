package com.github.dreamhead.moco.util;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class Preconditions {
    public static String checkNotNullOrEmpty(final String reference, final Object errorMessage) {
        if (isNullOrEmpty(reference)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }

        return reference;
    }

    public static void checkArgument(final boolean expression, final Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    private Preconditions() {
    }
}
