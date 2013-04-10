package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.*;

public class StartArgs extends ShutdownPortOption {
    private int port;
    private String configurationFile;

    public StartArgs(int port, Integer shutdownPort, String configurationFile) {
        super(shutdownPort);
        this.port = port;
        this.configurationFile = configurationFile;
    }

    public int getPort() {
        return port;
    }

    public String getConfigurationFile() {
        return configurationFile;
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

        String shutdownPort = cmd.getOptionValue("s");
        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one args allowed");
        }

        return new StartArgs((int)port, getShutdownPort(shutdownPort), config);
    }

    private static Options createMocoOptions() {
        Options options = new Options();
        options.addOption(configOption());
        options.addOption(portOption());
        options.addOption(shutdownPortOption());
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
        opt.setRequired(true);
        return opt;
    }
}
