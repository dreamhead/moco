package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.BootstrapTask;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.bootstrap.parser.HttpArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.RunnerFactory;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartTask implements BootstrapTask {
    private Logger logger = LoggerFactory.getLogger(StartTask.class);
    private final StartArgsParser startArgsParser;
    protected final RunnerFactory factory;

    public StartTask(final String shutdownKey) {
        this.factory = new RunnerFactory(shutdownKey);
        this.startArgsParser = new HttpArgsParser();
    }

    protected StartTask(final String shutdownKey, final StartArgsParser startArgsParser) {
        this.startArgsParser = startArgsParser;
        this.factory = new RunnerFactory(shutdownKey);
    }

    @Override
    public void run(final String[] args) {
        final Runner runner = createRunner(args);

        final Stopwatch stopwatch = Stopwatch.createStarted();
        runner.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                runner.stop();
                stopwatch.stop();
                logger.info("Total time: " + stopwatch);
            }
        });
    }

    protected Runner createRunner(final String[] args) {
        StartArgs startArgs = startArgsParser.parse(args);
        return factory.createRunner(startArgs);
    }
}
