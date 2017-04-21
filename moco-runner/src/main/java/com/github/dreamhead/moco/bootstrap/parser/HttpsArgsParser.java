package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.HttpsArg;
import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static com.github.dreamhead.moco.bootstrap.arg.HttpsArgs.httpsArgs;

public class HttpsArgsParser extends StartArgsParser {
    @Override
    protected StartArgs parseArgs(final CommandLine cmd) {
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
            throw new ParseArgException("only one arg not allowed");
        }

        return httpsArgs()
                .withPort(getPort(port))
                .withShutdownPort(getPort(shutdownPort))
                .withConfigurationFile(config)
                .withSettings(globalSettings)
                .withEnv(env)
                .withHttpsArg(httpsArg(cmd))
                .build();
    }

    private HttpsArg httpsArg(final CommandLine cmd) {
        String https = cmd.getOptionValue("https");
        String keystore = cmd.getOptionValue("keystore");
        String cert = cmd.getOptionValue("cert");
        if (https != null) {
            if (keystore == null || cert == null) {
                throw new ParseArgException("keystore and cert must be set for HTTPS");
            }

            return new HttpsArg(https, keystore, cert);
        }

        throw new ParseArgException("HTTPS arguments are expected");
    }

    @Override
    protected Options options() {
        return super.options()
                .addOption(settingsOption())
                .addOption(envOption())
                .addOption(httpsCertificate())
                .addOption(keyStore())
                .addOption(cert());
    }
}
