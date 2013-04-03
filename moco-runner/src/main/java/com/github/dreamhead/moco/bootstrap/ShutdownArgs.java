package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.*;

public class ShutdownArgs {
    private Integer shutdownPort;

    public ShutdownArgs(Integer shutdownPort) {
        this.shutdownPort = shutdownPort;
    }

    public Integer getShutdownPort() {
        return shutdownPort;
    }

    public static ShutdownArgs parse(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(createShutdownOptions(), args);
            return new ShutdownArgs(ShutdownPortOption.getShutdownPort(cmd.getOptionValue("s")));
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    private static Options createShutdownOptions() {
        Options options = new Options();
        options.addOption(ShutdownPortOption.shutdownPortOption());
        return options;
    }
}
