package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithStreams;

public abstract class FileRunner implements Runner {
    private Runner runner;

    protected abstract Runner newRunner();

    private FileRunner() {
        this.runner = newRunner();
    }

    public synchronized void restart() {
        this.runner.stop();
        this.runner = newRunner();
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

    public static FileRunner createConfigurationFileRunner(final Iterable<File> files, final StartArgs startArgs) {
        return new FileRunner() {
            @Override
            protected Runner newRunner() {
                return newJsonRunnerWithStreams(toInputStreams(files), startArgs);
            }
        };
    }

    public static FileRunner createSettingFileRunner(final File settingsFile, final StartArgs startArgs) {
        return new FileRunner() {
            @Override
            protected Runner newRunner() {
                return new SettingRunner(toInputStream(settingsFile), startArgs);
            }
        };
    }

    private static FileInputStream toInputStream(final File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new MocoException(e);
        }
    }

    private static Iterable<InputStream> toInputStreams(final Iterable<File> files) {
        return StreamSupport.stream(files.spliterator(), false)
                .map(FileRunner::toInputStream)
                .collect(Collectors.toList());
    }
}
