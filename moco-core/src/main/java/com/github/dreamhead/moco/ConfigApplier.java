package com.github.dreamhead.moco;

public interface ConfigApplier<T> {
    T apply(MocoConfig config);
}
