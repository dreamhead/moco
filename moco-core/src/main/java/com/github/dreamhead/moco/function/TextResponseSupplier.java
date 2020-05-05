package com.github.dreamhead.moco.function;

import java.util.function.Supplier;

public interface TextResponseSupplier extends Supplier<String> {
    default TextResponseFunction asFunction() {
        return request -> this.get();
    }
}
