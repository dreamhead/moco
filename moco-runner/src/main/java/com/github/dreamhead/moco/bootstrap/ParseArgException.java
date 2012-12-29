package com.github.dreamhead.moco.bootstrap;

public class ParseArgException extends RuntimeException {
    public ParseArgException(String s) {
        super(s);
    }

    public ParseArgException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
