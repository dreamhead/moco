package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.ResponseElement;

import java.util.function.Function;

public interface Transformer<U> extends ResponseElement {
    Transformer<U> transform(Function<U, U> transformer);
}
