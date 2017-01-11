package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.parser.GlobalSettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
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

public class SettingRunner implements Runner {
    private final GlobalSettingParser parser = new GlobalSettingParser();
    private final ImmutableList<GlobalSetting> globalSettings;
    private final Optional<String> env;
    private final StartArgs startArgs;
    private Runner runner;
    private final FluentIterable<File> files;

    public SettingRunner(final InputStream stream, final StartArgs args) {
        this.env = args.getEnv();
        this.globalSettings = parser.parse(stream);
        this.files = from(globalSettings).transform(toFile());
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

    private Predicate<? super GlobalSetting> byEnv(final Optional<String> env) {
        return new Predicate<GlobalSetting>() {
            @Override
            public boolean apply(final GlobalSetting globalSetting) {
                return !env.isPresent() || env.get().equalsIgnoreCase(globalSetting.getEnv());

            }
        };
    }

    private Function<GlobalSetting, RunnerSetting> toRunnerSetting() {
        return new Function<GlobalSetting, RunnerSetting>() {
            @Override
            public RunnerSetting apply(final GlobalSetting setting) {
                try {
                    return aRunnerSetting()
                            .withStream(new FileInputStream(setting.getInclude()))
                            .withContext(setting.getContext())
                            .withFileRoot(setting.getFileRoot())
                            .withRequest(setting.getRequest())
                            .withResponse(setting.getResponse())
                            .build();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void stop() {
        runner.stop();
    }

    private Function<? super GlobalSetting, File> toFile() {
        return new Function<GlobalSetting, File>() {
            @Override
            public File apply(final GlobalSetting input) {
                return new File(input.getInclude());
            }
        };
    }
}
