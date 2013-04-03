package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.Option;

public class ShutdownPortOption {
    public static Option shutdownPortOption() {
        Option opt = new Option("s", true, "shutdown port");
        opt.setType(Integer.class);
        opt.setRequired(false);
        return opt;
    }

    public static Integer getShutdownPort(String shutdownPort) {
        return shutdownPort == null ? null : Integer.valueOf(shutdownPort);
    }
}
