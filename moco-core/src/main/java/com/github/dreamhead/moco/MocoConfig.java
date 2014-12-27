package com.github.dreamhead.moco;

public interface MocoConfig<T> {
    boolean isFor(final String id);
    T apply(final T target);

    String FILE_ID = "file";
    String URI_ID = "uri";
    String REQUEST_ID = "request";
    String RESPONSE_ID = "response";
}
