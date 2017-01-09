package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.BootstrapTask;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.RunnerFactory;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StartTask implements BootstrapTask {
    private Logger logger = LoggerFactory.getLogger(StartTask.class);
    private final StartArgsParser startArgsParser;
    private final RunnerFactory factory;

    protected StartTask(final String shutdownKey, final StartArgsParser startArgsParser) {
        this.startArgsParser = startArgsParser;
        this.factory = new RunnerFactory(shutdownKey);
    }

    @Override
    public void run(final String[] args) {
        final Runner runner = createRunner(args);

        final Stopwatch stopwatch = Stopwatch.createStarted();
        runner.run();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                runner.stop();
                stopwatch.stop();
                logger.info("Total time: " + stopwatch);
            }
        }));
    }

    private Runner createRunner(final String[] args) {
        StartArgs startArgs = startArgsParser.parse(args);
        return factory.createRunner(startArgs);
    }
}
