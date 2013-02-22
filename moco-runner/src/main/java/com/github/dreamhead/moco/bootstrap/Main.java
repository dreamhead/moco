package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.DynamicRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.ShutdownMonitorRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.github.dreamhead.moco.bootstrap.BootArgs.parse;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String SHUTDOWN_FILE = ".shutdown_moco_hook";

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new ParseArgException("at least one arguments required");
            }

            String tmpDir = System.getProperty("java.io.tmpdir");
            File shutdownFile = new File(tmpDir, SHUTDOWN_FILE);

            if ("shutdown".equals(args[0])) {
                shutdown(shutdownFile.getAbsolutePath());
                System.exit(0);
            }

            BootArgs bootArgs = parse(args);
            Runner runner = new DynamicRunner(bootArgs.getConfigurationFile(), bootArgs.getPort());
            new ShutdownMonitorRunner(runner, shutdownFile.getAbsolutePath()).run();
        } catch (ParseArgException e) {
            help();
        }
    }

    private static void shutdown(String filename) {
        try {
            new File(filename).createNewFile();
        } catch (IOException e) {
            logger.error("failed to create shutdown file");
        }
    }

    private static void help() {
        System.out.println("moco -p port [configuration file]");
        System.exit(1);
    }
}
