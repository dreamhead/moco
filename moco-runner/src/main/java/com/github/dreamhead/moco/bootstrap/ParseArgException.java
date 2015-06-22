package com.github.dreamhead.moco.bootstrap;

public class ParseArgException extends RuntimeException {
    public ParseArgException(final String s) {
        super(s);
    }

    public ParseArgException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}
