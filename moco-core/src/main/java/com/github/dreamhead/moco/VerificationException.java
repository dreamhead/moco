package com.github.dreamhead.moco;

public class VerificationException extends RuntimeException {
    public VerificationException() {
        super();
    }

    public VerificationException(final String message) {
        super(message);
    }

    public VerificationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public VerificationException(final Throwable throwable) {
        super(throwable);
    }
}
