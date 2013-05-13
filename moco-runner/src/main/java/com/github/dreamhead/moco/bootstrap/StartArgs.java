package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.*;

public class StartArgs extends ShutdownPortOption {
    private int port;
    private String configurationFile;
    private String settings;

    public StartArgs(int port, Integer shutdownPort, String configurationFile, String globalSettings) {
        super(shutdownPort);
        this.port = port;
        this.configurationFile = configurationFile;
        this.settings = globalSettings;
    }

    public int getPort() {
        return port;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public boolean hasConfigurationFile() {
        return this.configurationFile != null;
    }

    public String getSettings() {
        return settings;
    }

    public static StartArgs parse(String... args) {
        try {
            return doParse(args);
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    private static StartArgs doParse(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(createMocoOptions(), args);
        long port = (Long)cmd.getParsedOptionValue("p");
        String config = cmd.getOptionValue("c");
        String globalSettings = cmd.getOptionValue("g");
        String shutdownPort = cmd.getOptionValue("s");

        if (config == null && globalSettings == null) {
            throw new ParseArgException("config or global setting is required");
        }

        if (config != null && globalSettings != null) {
            throw new ParseArgException("config and global settings can not be set at the same time");
        }

        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one args allowed");
        }

        return new StartArgs((int)port, getShutdownPort(shutdownPort), config, globalSettings);
    }

    private static Options createMocoOptions() {
        Options options = new Options();
        options.addOption(configOption());
        options.addOption(portOption());
        options.addOption(shutdownPortOption());
        options.addOption(settingsOption());
        return options;
    }

    private static Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Number.class);
        opt.setRequired(true);
        return opt;
    }

    private static Option configOption() {
        Option opt = new Option("c", true, "config");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    private static Option settingsOption() {
        Option opt = new Option("g", true, "global settings");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }
}
