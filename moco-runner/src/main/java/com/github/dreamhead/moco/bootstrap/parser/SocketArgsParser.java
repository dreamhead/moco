package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.ParseArgException;
import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import org.apache.commons.cli.*;

import static com.github.dreamhead.moco.bootstrap.ShutdownPortOption.shutdownPortOption;

public class SocketArgsParser extends StartArgsParser {
    @Override
    protected StartArgs parseArgs(CommandLine cmd) {
        String port = cmd.getOptionValue("p");
        String config = cmd.getOptionValue("c");
        String shutdownPort = cmd.getOptionValue("s");


        if (config == null) {
            throw new ParseArgException("config is required");
        }

        if (cmd.getArgs().length != 1) {
            throw new ParseArgException("only one args allowed");
        }

        return StartArgs.builder().withType(ServerType.SOCKET).withPort(getPort(port)).withShutdownPort(getPort(shutdownPort)).withConfigurationFile(config).build();
    }

    @Override
    protected Options createMocoOptions() {
        Options options = new Options();
        options.addOption(configOption());
        options.addOption(portOption());
        options.addOption(shutdownPortOption());
        return options;
    }
}
