package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.*;
import org.apache.commons.cli.*;

public class StartArgsParser {
    private ServerType serverType;

    public StartArgsParser(ServerType serverType) {
        this.serverType = serverType;
    }

    public StartArgs parse(String[] args) {
        try {
            return doParse(this.serverType, args);
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    private StartArgs doParse(ServerType type, String[] args) throws ParseException {
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

        return StartArgs.builder().withType(type).withPort(getPort(port)).withShutdownPort(getPort(shutdownPort)).withConfigurationFile(config).withSettings(globalSettings).withEnv(env).withHttpsArg(httpsArg(cmd)).build();
    }

    private HttpsArg httpsArg(CommandLine cmd) {
        String https = cmd.getOptionValue("https");
        String keystore = cmd.getOptionValue("keystore");
        String cert = cmd.getOptionValue("cert");
        if (https != null) {
            if (keystore == null || cert == null) {
                throw new ParseArgException("keystore and cert must be set for HTTPS");
            }

            return new HttpsArg(https, keystore, cert);
        }

        return null;
    }

    public Options createMocoOptions() {
        Options options = new Options();
        options.addOption(configOption());
        options.addOption(portOption());
        options.addOption(ShutdownPortOption.shutdownPortOption());
        options.addOption(settingsOption());
        options.addOption(envOption());
        options.addOption(httpsCertificate());
        options.addOption(keyStore());
        options.addOption(cert());
        return options;
    }

    private Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Number.class);
        opt.setRequired(false);
        return opt;
    }

    private Option configOption() {
        Option opt = new Option("c", true, "config");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    private Option settingsOption() {
        Option opt = new Option("g", true, "global settings");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    private Option envOption() {
        Option opt = new Option("e", true, "environment");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    private Option httpsCertificate() {
        Option option = new Option(null, "https", true, "Https certificate filename");
        option.setType(String.class);
        option.setRequired(false);
        return option;
    }

    private Option keyStore() {
        Option option = new Option(null, "keystore", true, "Key store password");
        option.setType(String.class);
        option.setRequired(false);
        return option;
    }

    private Option cert() {
        Option option = new Option(null, "cert", true, "Cert password");
        option.setType(String.class);
        option.setRequired(false);
        return option;
    }

    public static Integer getPort(String port) {
        return port == null ? null : Integer.valueOf(port);
    }
}
