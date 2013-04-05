package com.github.dreamhead.moco.bootstrap;

public class Bootstrap implements BootstrapTask {
    private static final int DEFAULT_SHUTDOWN_PORT = 9527;
    private static final String DEFAULT_SHUTDOWN_KEY = "_SHUTDOWN_MOCO_KEY";

    private BootstrapTask startTask = new StartTask(DEFAULT_SHUTDOWN_PORT, DEFAULT_SHUTDOWN_KEY);
    private BootstrapTask shutdownTask = new ShutdownTask(DEFAULT_SHUTDOWN_PORT, DEFAULT_SHUTDOWN_KEY);

    @Override
    public void run(String[] args) {
        try {
            if (args.length < 1) {
                throw new ParseArgException("at least one arguments required");
            }

            if ("shutdown".equals(args[0])) {
                shutdownTask.run(args);
                return;
            }

            startTask.run(args);
        } catch (ParseArgException e) {
            help();
        }
    }

    private void help() {
        System.out.println("moco -p port {-s [shutdown port]} [configuration file]");
        System.exit(1);
    }
}
