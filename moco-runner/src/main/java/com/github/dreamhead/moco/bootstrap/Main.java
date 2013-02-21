package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.DynamicRunner;

import static com.github.dreamhead.moco.bootstrap.BootArgs.parse;

public class Main {
    public static void main(String[] args) {
        try {
            BootArgs bootArgs = parse(args);
            new DynamicRunner(bootArgs.getConfigurationFile(), bootArgs.getPort()).run();
        } catch (ParseArgException e) {
            help();
        }
    }

    private static void help() {
        System.out.println("moco -p port [configuration file]");
        System.exit(1);
    }
}
