package com.github.dreamhead.moco.resource;

public class IdFactory {
    public static Identifiable id(final String id) {
        return new Identifiable() {
            @Override
            public String id() {
                return id;
            }
        };
    }

    public static Identifiable id(final Resource resource) {
        return new Identifiable() {
            @Override
            public String id() {
                return resource.id();
            }
        };
    }

    private IdFactory() {}
}
