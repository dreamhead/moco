package com.github.dreamhead.moco.bootstrap;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements BootstrapTask {
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private static final String DEFAULT_SHUTDOWN_KEY = "_SHUTDOWN_MOCO_KEY";

    private final ImmutableMap<String, BootstrapTask> tasks = ImmutableMap.of(
            "start", new StartTask(DEFAULT_SHUTDOWN_KEY, ServerType.HTTP),
            "shutdown", new ShutdownTask(DEFAULT_SHUTDOWN_KEY),
            "http", new StartTask(DEFAULT_SHUTDOWN_KEY, ServerType.HTTP),
            "https", new StartTask(DEFAULT_SHUTDOWN_KEY, ServerType.HTTPS),
            "socket", new StartTask(DEFAULT_SHUTDOWN_KEY, ServerType.SOCKET)
    );

    @Override
    public void run(final String[] args) {
        try {
            if (args.length < 1) {
                throw new ParseArgException("task name needs to be specified");
            }

            BootstrapTask task = tasks.get(args[0]);
            if (task == null) {
                throw new ParseArgException("unknown task");
            }

            task.run(args);
        } catch (ParseArgException e) {
            help();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void help() {
        System.out.println(StartArgs.help());
    }
}
