package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.bootstrap.tasks.ShutdownTask;
import com.github.dreamhead.moco.bootstrap.tasks.StartTask;
import com.github.dreamhead.moco.bootstrap.tasks.VersionTask;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap implements BootstrapTask {
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private static final String DEFAULT_SHUTDOWN_KEY = "_SHUTDOWN_MOCO_KEY";

    private final ImmutableMap<String, BootstrapTask> tasks = ImmutableMap.<String, BootstrapTask>builder()
            .put("start", StartTask.http(DEFAULT_SHUTDOWN_KEY))
            .put("shutdown", new ShutdownTask(DEFAULT_SHUTDOWN_KEY))
            .put("http", StartTask.http(DEFAULT_SHUTDOWN_KEY))
            .put("https", StartTask.https(DEFAULT_SHUTDOWN_KEY))
            .put("socket", StartTask.socket(DEFAULT_SHUTDOWN_KEY))
            .put("version", new VersionTask())
            .build();

    @Override
    public final void run(final String[] args) {
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
