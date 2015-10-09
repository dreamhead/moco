package com.github.dreamhead.moco.resource;

public final class IdFactory {
    public static Identifiable id(final String id) {
        return new Identifiable() {
            @Override
            public String id() {
                return id;
            }
        };
    }

    private IdFactory() {
    }
}
