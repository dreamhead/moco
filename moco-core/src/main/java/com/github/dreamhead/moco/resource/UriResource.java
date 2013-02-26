package com.github.dreamhead.moco.resource;

public class UriResource implements Resource {
    private final String uri;

    public UriResource(String uri) {
        this.uri = uri;
    }

    @Override
    public String id() {
        return "uri";
    }

    @Override
    public byte[] asByteArray() {
        return this.uri.getBytes();
    }
}
