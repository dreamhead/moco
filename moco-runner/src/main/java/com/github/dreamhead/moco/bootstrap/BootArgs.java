package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.*;

public class BootArgs {
    private int port;
    private Integer shutdownPort;
    private String configurationFile;

    public BootArgs(int port, Integer shutdownPort, String configurationFile) {
        this.port = port;
        this.shutdownPort = shutdownPort;
        this.configurationFile = configurationFile;
    }

    public int getPort() {
        return port;
    }

    public Integer getShutdownPort() {
        return shutdownPort;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public static BootArgs parse(String[] args) {
        try {
            return doParse(args);
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    private static BootArgs doParse(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(createMocoOptions(), args);
        int port = Integer.parseInt(cmd.getOptionValue("p"));
        String shutdownPort = cmd.getOptionValue("s");
        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one args allowed");
        }
        return new BootArgs(port, getShutdownPort(shutdownPort), cmd.getArgs()[0]);
    }

    private static Integer getShutdownPort(String shutdownPort) {
        return shutdownPort == null ? null : Integer.valueOf(shutdownPort);
    }

    private static Options createMocoOptions() {
        Options options = new Options();
        options.addOption(portOption());
        options.addOption(shutdownPortOption());
        return options;
    }

    private static Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Integer.class);
        opt.setRequired(true);
        return opt;
    }

    private static Option shutdownPortOption() {
        Option opt = new Option("s", true, "shutdown port");
        opt.setType(Integer.class);
        opt.setRequired(false);
        return opt;
    }
}
