package com.github.dreamhead.moco.bootstrap;

import com.google.common.base.Optional;
import org.apache.commons.cli.Option;

public class ShutdownPortOption {
    private final Optional<Integer> shutdownPort;

    public ShutdownPortOption(final Integer shutdownPort) {
        this.shutdownPort = Optional.fromNullable(shutdownPort);
    }

    public Optional<Integer> getShutdownPort() {
        return shutdownPort;
    }

    public static Option shutdownPortOption() {
        Option opt = new Option("s", true, "shutdown port");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }
}
