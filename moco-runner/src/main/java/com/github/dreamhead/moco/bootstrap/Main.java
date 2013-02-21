package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.DynamicRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.ShutdownMonitorRunner;

import static com.github.dreamhead.moco.bootstrap.BootArgs.parse;

public class Main {
    private static final String SHUTDOWN_FILE = ".shutdown_moco_hook";

    public static void main(String[] args) {
        try {
            BootArgs bootArgs = parse(args);
            Runner runner = new DynamicRunner(bootArgs.getConfigurationFile(), bootArgs.getPort());
            new ShutdownMonitorRunner(runner, SHUTDOWN_FILE).run();
        } catch (ParseArgException e) {
            help();
        }
    }

    private static void help() {
        System.out.println("moco -p port [configuration file]");
        System.exit(1);
    }
}
