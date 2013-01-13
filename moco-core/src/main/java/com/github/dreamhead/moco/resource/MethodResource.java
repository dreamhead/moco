package com.github.dreamhead.moco.resource;

public class MethodResource implements Resource {
    private String method;

    public MethodResource(String method) {
        this.method = method.toUpperCase();
    }

    @Override
    public String id() {
        return "method";
    }

    @Override
    public byte[] asByteArray() {
        return method.getBytes();
    }
}
