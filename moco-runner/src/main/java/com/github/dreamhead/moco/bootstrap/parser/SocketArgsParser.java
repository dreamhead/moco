package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import org.apache.commons.cli.CommandLine;

import static com.github.dreamhead.moco.bootstrap.arg.SocketArgs.socketArgs;

public class SocketArgsParser extends StartArgsParser {
    @Override
    protected StartArgs parseArgs(final CommandLine cmd) {
        String port = cmd.getOptionValue("p");
        String config = cmd.getOptionValue("c");
        String shutdownPort = cmd.getOptionValue("s");

        if (config == null) {
            throw new ParseArgException("config is required");
        }

        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one arg not allowed");
        }

        return socketArgs()
                .withPort(getPort(port))
                .withShutdownPort(getPort(shutdownPort))
                .withConfigurationFile(config)
                .build();
    }
}
