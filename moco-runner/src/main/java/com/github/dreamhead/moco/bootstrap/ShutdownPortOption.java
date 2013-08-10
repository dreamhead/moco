package com.github.dreamhead.moco.bootstrap;

import com.google.common.base.Optional;
import org.apache.commons.cli.Option;

public class ShutdownPortOption {
    private Optional<Integer> shutdownPort;

    public ShutdownPortOption(Integer shutdownPort) {
        this.shutdownPort = Optional.fromNullable(shutdownPort);
    }

    public boolean hasShutdonwPort() {
        return shutdownPort.isPresent();
    }

    public int getShutdownPort(int defaultShutDownPort) {
        return shutdownPort.or(defaultShutDownPort);
    }

    public static Option shutdownPortOption() {
        Option opt = new Option("s", true, "shutdown port");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    public static Integer getShutdownPort(String shutdownPort) {
        return shutdownPort == null ? null : Integer.valueOf(shutdownPort);
    }
}
