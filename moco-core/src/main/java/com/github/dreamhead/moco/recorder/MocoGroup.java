package com.github.dreamhead.moco.recorder;

public class MocoGroup implements RecorderConfig {
    private final String name;

    public MocoGroup(final String name) {
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
