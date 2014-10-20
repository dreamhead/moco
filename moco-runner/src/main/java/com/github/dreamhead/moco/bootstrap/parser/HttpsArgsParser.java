package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.ShutdownPortOption;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import org.apache.commons.cli.*;

public class HttpsArgsParser extends StartArgsParser {
    @Override
    protected StartArgs parseArgs(CommandLine cmd) {
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

        return StartArgs.builder().withType(ServerType.HTTPS).withPort(getPort(port)).withShutdownPort(getPort(shutdownPort)).withConfigurationFile(config).withSettings(globalSettings).withEnv(env).withHttpsArg(httpsArg(cmd)).build();
    }

    @Override
    protected Options createMocoOptions() {
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
}
