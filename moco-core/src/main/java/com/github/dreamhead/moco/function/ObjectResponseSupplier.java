package com.github.dreamhead.moco.function;

import java.util.function.Supplier;

public interface ObjectResponseSupplier extends Supplier<Object> {
    default ObjectResponseFunction asFunction() {
        return request -> this.get();
    }
}
