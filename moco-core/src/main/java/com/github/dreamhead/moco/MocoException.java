package com.github.dreamhead.moco;

public class MocoException extends RuntimeException {

    public MocoException(final String message) {
        super(message);
    }

    public MocoException(final Throwable cause) {
        super(cause);
    }

    public MocoException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
