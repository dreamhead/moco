package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.RunnerFactory;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class StartTask implements BootstrapTask {
    private Logger logger = LoggerFactory.getLogger(StartTask.class);
    private final RunnerFactory factory;
    private final ServerType type;

    public StartTask(String shutdownKey, ServerType type) {
        this.factory = new RunnerFactory(shutdownKey);
        this.type = type;
    }

    @Override
    public void run(final String[] args) {
        StartArgs startArgs = parse(type, args);

        final Runner runner = factory.createRunner(startArgs);

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
}
