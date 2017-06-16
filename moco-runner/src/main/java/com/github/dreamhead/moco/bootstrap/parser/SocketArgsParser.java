package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static com.github.dreamhead.moco.bootstrap.ShutdownPortOption.shutdownPortOption;
import static com.github.dreamhead.moco.bootstrap.arg.SocketArgs.socketArgs;

public class SocketArgsParser extends StartArgsParser {
    @Override
    protected StartArgs parseArgs(final CommandLine cmd) {
        String port = cmd.getOptionValue("p");
        String[] configs = cmd.getOptionValues("c");
        String shutdownPort = cmd.getOptionValue("s");

        if (configs == null) {
            throw new ParseArgException("config is required");
        }

        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one arg not allowed");
        }

        return socketArgs()
                .withPort(getPort(port))
                .withShutdownPort(getPort(shutdownPort))
                .withConfigurationFile(configs)
                .build();
    }

    @Override
    protected Options options() {
        Options options = new Options();
        options.addOption(configOption());
        options.addOption(portOption());
        options.addOption(shutdownPortOption());
        return options;
    }
}
