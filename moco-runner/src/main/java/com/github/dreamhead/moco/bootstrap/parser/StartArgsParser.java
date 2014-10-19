package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.*;
import org.apache.commons.cli.*;

public abstract class StartArgsParser {
    protected abstract StartArgs doParse(String[] args) throws ParseException;

    public StartArgs parse(String[] args) {
        try {
            return doParse(args);
        } catch (ParseException e) {
            throw new ParseArgException("fail to parse arguments", e);
        }
    }

    protected HttpsArg httpsArg(CommandLine cmd) {
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

    public static Integer getPort(String port) {
        return port == null ? null : Integer.valueOf(port);
    }
}
