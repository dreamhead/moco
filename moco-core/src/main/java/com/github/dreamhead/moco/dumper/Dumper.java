package com.github.dreamhead.moco.dumper;

public interface Dumper<T> {
    String dump(final T message);
}
