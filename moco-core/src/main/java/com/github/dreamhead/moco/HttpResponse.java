package com.github.dreamhead.moco;

public interface HttpResponse extends Response, HttpMessage {
    int getStatus();
}
