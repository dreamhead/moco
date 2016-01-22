package com.github.dreamhead.moco;

/**
 * Created by DevFactory on 1/22/16.
 */
public class MocoException extends RuntimeException {

    public MocoException(final String message) {
        super(message);
    }

    public MocoException(Throwable cause) {
        super(cause);
    }

    public MocoException(String message, Throwable cause) {
        super(message, cause);
    }

}
