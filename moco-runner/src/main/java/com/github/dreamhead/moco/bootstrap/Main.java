package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.DynamicRunner;

import java.io.IOException;

import static com.github.dreamhead.moco.bootstrap.BootArgs.parse;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            BootArgs bootArgs = parse(args);
            new DynamicRunner().run(bootArgs.getConfigurationFile(), bootArgs.getPort());
        } catch (ParseArgException e) {
            help();
        }
    }

    private static void help() {
        System.out.println("moco -p port [configuration file]");
        System.exit(1);
    }
}
