package com.github.dreamhead.moco.internal.dumper;

public interface Dumper<T> {
    String dump(T message);
}
