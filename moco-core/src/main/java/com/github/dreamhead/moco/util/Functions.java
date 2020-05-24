package com.github.dreamhead.moco.util;

import java.util.function.Function;

public final class Functions {
    public static <T, R> R checkApply(final Function<T, R> function, final T argument) {
        R result = function.apply(argument);
        if (result == null) {
            throw new NullPointerException("Null returned from function");
        }

        return result;
    }

    private Functions() {
    }
}
