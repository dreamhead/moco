package com.github.dreamhead.moco.recorder;

public class RecorderGroup implements RecorderConfig {
    private String name;

    public RecorderGroup(final String name) {
        this.name = name;
    }

    @Override
    public boolean isFor(String name) {
        return GROUP.equalsIgnoreCase(name);
    }

    public String getName() {
        return name;
    }
}
