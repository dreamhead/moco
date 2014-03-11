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

    public StartTask(String defaultShutdownKey) {
        this.factory = new RunnerFactory(defaultShutdownKey);
    }

    @Override
    public void run(String[] args) {
        StartArgs startArgs = parse(args);

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
