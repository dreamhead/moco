package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.JsonRunner;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(createMocoOptions(), args);
            int port = Integer.parseInt(cmd.getOptionValue("p"));
            if (cmd.getArgs().length != 1) {
                help();
            }

            new JsonRunner().run(new FileInputStream(cmd.getArgs()[0]), port);
        } catch (ParseException e) {
            help();
        }
    }

    private static Options createMocoOptions() {
        Options options = new Options();
        options.addOption(portOption());
        return options;
    }

    private static Option portOption() {
        Option opt = new Option("p", true, "port");
        opt.setType(Integer.class);
        opt.setRequired(true);
        return opt;
    }

    private static void help() {
        System.out.println("moco -p port [configuration file]");
        System.exit(1);
    }
}
