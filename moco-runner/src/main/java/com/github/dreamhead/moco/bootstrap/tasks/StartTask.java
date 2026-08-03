package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.BootstrapTask;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.bootstrap.parser.HttpArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.HttpsArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.RunnerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class StartTask implements BootstrapTask {
    private Logger logger = LoggerFactory.getLogger(StartTask.class);
    private final StartArgsParser startArgsParser;
    private final RunnerFactory factory;

    private StartTask(final String shutdownKey, final StartArgsParser startArgsParser) {
        this.startArgsParser = startArgsParser;
        this.factory = new RunnerFactory(shutdownKey);
    }

    @Override
    public void run(final String[] args) {
        final Runner runner = createRunner(args);

        final long startedAt = System.nanoTime();
        runner.run();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            runner.stop();
            logger.info("Total time: {} ms", Duration.ofNanos(System.nanoTime() - startedAt).toMillis());
        }));
    }

    private Runner createRunner(final String[] args) {
        StartArgs startArgs = startArgsParser.parse(args);
        return factory.createRunner(startArgs);
    }

    public static BootstrapTask http(final String shutdownKey) {
        return new StartTask(shutdownKey, new HttpArgsParser());
    }

    public static BootstrapTask https(final String shutdownKey) {
        return new StartTask(shutdownKey, new HttpsArgsParser());
    }
}
