package com.github.dreamhead.moco.recorder;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        MocoGroup mocoGroup = (MocoGroup) that;
        return Objects.equal(name, mocoGroup.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
