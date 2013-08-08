package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithStreams;
import static com.google.common.collect.ImmutableList.of;

public abstract class FileRunner implements Runner {
    protected final File file;
    protected final int port;
    private Runner runner;

    protected abstract Runner createRunner();

    protected FileRunner(File file, int port) {
        this.file = file;
        this.port = port;
        this.runner = createRunner();
    }

    public void restart() {
        this.runner.stop();
        this.runner = createRunner();
        this.runner.run();
    }

    public Runner getRunner() {
        return runner;
    }

    @Override
    public void run() {
        this.runner.run();
    }

    @Override
    public void stop() {
        this.runner.stop();
    }

    public static FileRunner createConfigurationFileRunner(final File file, final int port) {
        return new FileRunner(file, port) {
            @Override
            protected Runner createRunner() {
                return newJsonRunnerWithStreams(of(toInputStream(file)), port);
            }
        };
    }

    public static FileRunner createSettingFileRunner(final File settingsFile, final StartArgs startArgs) {
        return new FileRunner(settingsFile, startArgs.getPort()) {
            @Override
            protected Runner createRunner() {
                return new SettingRunner(toInputStream(file), startArgs);
            }
        };
    }

    private static FileInputStream toInputStream(final File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}