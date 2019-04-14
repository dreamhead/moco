package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.parser.GlobalSettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithSetting;
import static com.github.dreamhead.moco.runner.RunnerSetting.aRunnerSetting;
import static com.google.common.collect.FluentIterable.from;

public final class SettingRunner implements Runner {
    private final GlobalSettingParser parser = new GlobalSettingParser();
    private final ImmutableList<GlobalSetting> globalSettings;
    private final String env;
    private final StartArgs startArgs;
    private Runner runner;
    private final FluentIterable<File> files;

    public SettingRunner(final InputStream stream, final StartArgs args) {
        this.env = args.getEnv().orNull();
        this.globalSettings = parser.parse(stream);
        this.files = from(globalSettings).transformAndConcat(toFiles());
        this.startArgs = args;
    }

    public Iterable<File> getFiles() {
        return files;
    }

    public void run() {
        runner = newJsonRunnerWithSetting(from(globalSettings)
                .filter(byEnv(this.env))
                .transform(toRunnerSetting()), startArgs);
        runner.run();
    }

    private Predicate<? super GlobalSetting> byEnv(final String env) {
        return new Predicate<GlobalSetting>() {
            @Override
            public boolean apply(final GlobalSetting globalSetting) {
                return env == null || env.equalsIgnoreCase(globalSetting.getEnv());

            }
        };
    }

    private Function<GlobalSetting, RunnerSetting> toRunnerSetting() {
        return new Function<GlobalSetting, RunnerSetting>() {
            @Override
            public RunnerSetting apply(final GlobalSetting setting) {
                return aRunnerSetting()
                        .addStreams(from(setting.includes()).transform(toStream()).toList())
                        .withContext(setting.getContext())
                        .withFileRoot(setting.getFileRoot())
                        .withRequest(setting.getRequest())
                        .withResponse(setting.getResponse())
                        .build();
            }
        };
    }

    private Function<String, InputStream> toStream() {
        return new Function<String, InputStream>() {
            @Override
            public InputStream apply(final String input) {
                try {
                    return new FileInputStream(input);
                } catch (FileNotFoundException e) {
                    throw new MocoException(e);
                }
            }
        };
    }

    public void stop() {
        runner.stop();
    }

    private Function<? super GlobalSetting, Iterable<? extends File>> toFiles() {
        return new Function<GlobalSetting, Iterable<? extends File>>() {
            @Override
            public Iterable<? extends File> apply(final GlobalSetting input) {
                return from(input.includes()).transform(toFile());
            }
        };
    }

    private Function<String, File> toFile() {
        return new Function<String, File>() {
            @Override
            public File apply(final String input) {
                return new File(input);
            }
        };
    }
}
