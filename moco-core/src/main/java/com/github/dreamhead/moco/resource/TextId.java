package com.github.dreamhead.moco.resource;

public class TextId implements Identifiable {
    private String id;

    private TextId(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    public static TextId id(String id) {
        return new TextId(id);
    }
}
