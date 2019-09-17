package com.github.dreamhead.moco;

import java.util.Optional;

public interface RequestExtractor<T> {
    Optional<T> extract(Request request);
}
