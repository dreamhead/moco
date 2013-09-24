package com.github.dreamhead.moco.bootstrap;

import com.google.common.base.Optional;
import org.apache.commons.cli.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static com.google.common.base.Optional.fromNullable;

public class StartArgs extends ShutdownPortOption {
    private final Optional<Integer> port;
    private final Optional<String> configurationFile;
    private final Optional<String> settings;
    private final Optional<String> env;

    public StartArgs(Integer port, Integer shutdownPort, String configurationFile, String globalSettings, String env) {
        super(shutdownPort);
        this.port = fromNullable(port);
        this.configurationFile = fromNullable(configurationFile);
        this.settings = fromNullable(globalSettings);
        this.env = fromNullable(env);
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public Optional<String> getConfigurationFile() {
        return configurationFile;
    }

    public boolean hasConfigurationFile() {
        return this.configurationFile.isPresent();
    }

    public Optional<String> getSettings() {
        return settings;
    }

    public Optional<String> getEnv() {
        return env;
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
        String port = cmd.getOptionValue("p");
        String config = cmd.getOptionValue("c");
        String globalSettings = cmd.getOptionValue("g");
        String shutdownPort = cmd.getOptionValue("s");
        String env = cmd.getOptionValue("e");

        if (config == null && globalSettings == null) {
            throw new ParseArgException("config or global setting is required");
        }

        if (config != null && globalSettings != null) {
            throw new ParseArgException("config and global settings can not be set at the same time");
        }

        if (globalSettings == null && env != null) {
            throw new ParseArgException("environment must be configured with global settings");
        }

        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one args allowed");
        }

        return new StartArgs(getPort(port), getShutdownPort(shutdownPort), config, globalSettings, env);
    }

    private static Options createMocoOptions() {
        Options options = new Options();
        options.addOption(configOption());
        options.addOption(portOption());
        options.addOption(shutdownPortOption());
        options.addOption(settingsOption());
        options.addOption(envOption());
        return options;
    }

    private static Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Number.class);
        opt.setRequired(false);
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

    private static Option envOption() {
        Option opt = new Option("e", true, "environment");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    public static Integer getPort(String port) {
        return port == null ? null : Integer.valueOf(port);
    }

    public static String help() {
        HelpFormatter formatter = new HelpFormatter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(os);
        formatter.printHelp(writer, HelpFormatter.DEFAULT_WIDTH, "moco start [options]", null, createMocoOptions(), HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        writer.flush();
        return new String(os.toByteArray());
    }
}
