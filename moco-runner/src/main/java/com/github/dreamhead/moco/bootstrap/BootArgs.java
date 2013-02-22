package com.github.dreamhead.moco.bootstrap;

import org.apache.commons.cli.*;

public class BootArgs {
    private int port;
    private String configurationFile;

    public BootArgs(int port, String configurationFile) {
        this.port = port;
        this.configurationFile = configurationFile;
    }

    public int getPort() {
        return port;
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
        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one args allowed");
        }
        return new BootArgs(port, cmd.getArgs()[0]);
    }

    private static Options createMocoOptions() {
        Options options = new Options();
        options.addOption(portOption());
        return options;
    }

    private static Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Integer.class);
        opt.setRequired(true);
        return opt;
    }
}
