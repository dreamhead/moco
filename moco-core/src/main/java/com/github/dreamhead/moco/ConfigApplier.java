package com.github.dreamhead.moco;

public interface ConfigApplier<T> {
    T apply(final MocoConfig config);
}
