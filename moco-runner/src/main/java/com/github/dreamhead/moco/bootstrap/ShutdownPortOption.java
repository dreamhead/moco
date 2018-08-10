package com.github.dreamhead.moco.bootstrap;

import com.google.common.base.Optional;
import org.apache.commons.cli.Option;

public abstract class ShutdownPortOption {
    private final Integer shutdownPort;

    protected ShutdownPortOption(final Integer shutdownPort) {
        this.shutdownPort = shutdownPort;
    }

    public final Optional<Integer> getShutdownPort() {
        return Optional.fromNullable(shutdownPort);
    }

    public static Option shutdownPortOption() {
        Option opt = new Option("s", true, "shutdown port");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }
}
