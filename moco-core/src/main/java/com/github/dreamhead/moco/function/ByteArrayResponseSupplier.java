package com.github.dreamhead.moco.function;

import java.util.function.Supplier;

public interface ByteArrayResponseSupplier extends Supplier<byte[]> {
    default ByteArrayResponseFunction asFunction() {
        return request -> this.get();
    }
}
