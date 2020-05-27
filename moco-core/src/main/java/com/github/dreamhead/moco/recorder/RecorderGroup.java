package com.github.dreamhead.moco.recorder;

public class RecorderGroup implements RecorderConfig {
    private String name;

    public RecorderGroup(final String name) {
        this.name = name;
    }

    @Override
    public final boolean isFor(final String name) {
        return GROUP.equalsIgnoreCase(name);
    }

    public final String getName() {
        return name;
    }
}
