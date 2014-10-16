package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import org.apache.commons.cli.*;

public class ShutdownArgs extends ShutdownPortOption {
    public ShutdownArgs(Integer shutdownPort) {
        super(shutdownPort);
    }

    public static ShutdownArgs parse(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(createShutdownOptions(), args);
            return new ShutdownArgs(StartArgsParser.getPort(cmd.getOptionValue("s")));
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    private static Options createShutdownOptions() {
        Options options = new Options();
        Option option = shutdownPortOption();
        option.setRequired(true);
        options.addOption(option);
        return options;
    }
}
