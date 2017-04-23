package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class StartArgsParser {
    protected abstract Options options();
    protected abstract StartArgs parseArgs(final CommandLine cmd);

    public StartArgs parse(final String[] args) {
        try {
            return doParse(args);
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    private StartArgs doParse(final String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options(), args);
        return parseArgs(cmd);
    }

    protected Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Number.class);
        opt.setRequired(false);
        return opt;
    }

    protected Option configOption() {
        Option opt = new Option("c", true, "config");
        opt.setType(String.class);
        opt.setRequired(false);
        opt.setArgs(Option.UNLIMITED_VALUES);
        return opt;
    }

    protected Option settingsOption() {
        Option opt = new Option("g", true, "global settings");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    protected Option envOption() {
        Option opt = new Option("e", true, "environment");
        opt.setType(String.class);
        opt.setRequired(false);
        return opt;
    }

    protected Option httpsCertificate() {
        Option option = new Option(null, "https", true, "Https certificate filename");
        option.setType(String.class);
        option.setRequired(false);
        return option;
    }

    protected Option keyStore() {
        Option option = new Option(null, "keystore", true, "Key store password");
        option.setType(String.class);
        option.setRequired(false);
        return option;
    }

    protected Option cert() {
        Option option = new Option(null, "cert", true, "Cert password");
        option.setType(String.class);
        option.setRequired(false);
        return option;
    }

    public static Integer getPort(final String port) {
        if (port == null) {
            return null;
        }

        return Integer.valueOf(port);
    }
}
