package com.github.dreamhead.moco.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements BootstrapTask {
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private static final int DEFAULT_SHUTDOWN_PORT = 9527;
    private static final String DEFAULT_SHUTDOWN_KEY = "_SHUTDOWN_MOCO_KEY";

    private final BootstrapTask startTask = new StartTask(DEFAULT_SHUTDOWN_PORT, DEFAULT_SHUTDOWN_KEY);
    private final BootstrapTask shutdownTask = new ShutdownTask(DEFAULT_SHUTDOWN_PORT, DEFAULT_SHUTDOWN_KEY);

    @Override
    public void run(String[] args) {
        try {
            if (args.length < 1) {
                throw new ParseArgException("task name needs to be specified");
            }

            if ("start".equals(args[0])) {
                startTask.run(args);
                return;
            }

            if ("shutdown".equals(args[0])) {
                shutdownTask.run(args);
                return;
            }

            help();
        } catch (ParseArgException e) {
            help();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void help() {
        System.out.println("moco start {-p port} -c [configuration file] {-s [shutdown port]}");
        System.out.println("or");
        System.out.println("moco start {-p port} -g [global settings file] {-e [environment]} {-s [shutdown port]}");
    }
}
