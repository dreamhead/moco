package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.parser.GlobalSettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithSetting;
import static com.github.dreamhead.moco.runner.RunnerSetting.aRunnerSetting;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toList;

public final class SettingRunner implements Runner {
    private final GlobalSettingParser parser = new GlobalSettingParser();
    private final ImmutableList<GlobalSetting> globalSettings;
    private final String env;
    private final StartArgs startArgs;
    private Runner runner;
    private final List<File> files;

    public SettingRunner(final InputStream stream, final StartArgs args) {
        this.env = args.getEnv().orElse(null);
        this.globalSettings = parser.parse(stream);
        this.files = globalSettings.stream()
                .flatMap(toFiles())
                .collect(toList());
        this.startArgs = args;
    }

    public Iterable<File> getFiles() {
        return files;
    }

    public void run() {
        runner = newJsonRunnerWithSetting(
                globalSettings.stream()
                        .filter(byEnv(this.env))
                        .map(toRunnerSetting())
                        .collect(toList()), startArgs);
        runner.run();
    }

    private Predicate<? super GlobalSetting> byEnv(final String env) {
        return globalSetting -> env == null || env.equalsIgnoreCase(globalSetting.getEnv());
    }

    private Function<GlobalSetting, RunnerSetting> toRunnerSetting() {
        return setting -> aRunnerSetting()
                .addStreams(setting.includes().stream()
                        .map(toStream())
                        .collect(toImmutableList()))
                .withContext(setting.getContext())
                .withFileRoot(setting.getFileRoot())
                .withRequest(setting.getRequest())
                .withResponse(setting.getResponse())
                .build();
    }

    private Function<String, InputStream> toStream() {
        return input -> {
            try {
                return new FileInputStream(input);
            } catch (FileNotFoundException e) {
                throw new MocoException(e);
            }
        };
    }

    public void stop() {
        runner.stop();
    }

    private Function<? super GlobalSetting, Stream<? extends File>> toFiles() {
        return input -> input.includes().stream().map(File::new);
    }
}
