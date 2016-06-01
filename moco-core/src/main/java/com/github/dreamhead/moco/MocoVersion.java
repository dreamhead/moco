package com.github.dreamhead.moco;

public final class MocoVersion {
    public static final String VERSION;

    static {
        VERSION = MocoVersion.class.getPackage().getImplementationVersion();
    }

    private MocoVersion() {
    }
}
