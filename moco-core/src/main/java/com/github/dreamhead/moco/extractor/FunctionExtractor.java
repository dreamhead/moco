package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;

import java.util.Optional;
import java.util.function.Function;

public class FunctionExtractor<T> implements RequestExtractor<T> {
    private final Function<Request, T> function;

    public FunctionExtractor(final Function<Request, T> function) {
        this.function = function;
    }

    @Override
    public final Optional<T> extract(final Request request) {
        return Optional.ofNullable(this.function.apply(request));
    }
}
