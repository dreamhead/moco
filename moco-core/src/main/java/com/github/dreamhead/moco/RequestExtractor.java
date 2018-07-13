package com.github.dreamhead.moco;

import com.google.common.base.Optional;

public interface RequestExtractor<T> {
    Optional<T> extract(Request request);
}
