package com.github.dreamhead.moco.resource;

import java.util.function.Function;

public interface Transformer<T, U> {
    T transform(Function<U, U> transformer);
}
