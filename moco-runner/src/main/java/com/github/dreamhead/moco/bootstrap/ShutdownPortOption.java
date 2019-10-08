package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.Option;

import java.util.Optional;

public abstract class ShutdownPortOption {
    private final Integer shutdownPort;

    protected ShutdownPortOption(final Integer shutdownPort) {
        this.shutdownPort = shutdownPort;
    }

    public final Optional<Integer> getShutdownPort() {
        return Optional.ofNullable(shutdownPort);
    }

    public static Option shutdownPortOption() {
        Option opt = new Option("s", true, "shutdown port");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }
}
