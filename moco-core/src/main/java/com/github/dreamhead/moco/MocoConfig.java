package com.github.dreamhead.moco;

public interface MocoConfig<T> {
    boolean isFor(String id);
    T apply(T target);

    String FILE_ID = "file";
    String URI_ID = "uri";
    String REQUEST_ID = "request";
    String RESPONSE_ID = "response";
}
