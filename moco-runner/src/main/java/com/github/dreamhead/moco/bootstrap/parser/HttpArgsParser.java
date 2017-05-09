package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;

public class HttpArgsParser extends StartArgsParser {
    @Override
    protected StartArgs parseArgs(final CommandLine cmd) {
        String port = cmd.getOptionValue("p");
        String config = cmd.getOptionValue("c");
        String globalSettings = cmd.getOptionValue("g");
        String shutdownPort = cmd.getOptionValue("s");
        String env = cmd.getOptionValue("e");
        boolean watchService = cmd.hasOption("watch-service");

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

        return httpArgs()
                .withPort(getPort(port))
                .withShutdownPort(getPort(shutdownPort))
                .withConfigurationFile(config)
                .withSettings(globalSettings)
                .withEnv(env)
                .withWatchService(watchService)
                .build();
    }

    @Override
    protected Options options() {
        return super.options()
                .addOption(settingsOption())
                .addOption(envOption());
    }
}
