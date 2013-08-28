package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
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
import static com.google.common.collect.FluentIterable.from;

public class SettingRunner implements Runner {
    private static final GlobalSettingParser settingParser = new GlobalSettingParser();
    private final int port;
    private final ImmutableList<GlobalSetting> globalSettings;
    private final Optional<String> env;
    private JsonRunner jsonRunner;
    private final FluentIterable<File> files;

    public SettingRunner(InputStream stream, StartArgs args) {
        this.port = args.getPort();
        this.env = args.getEnv();
        this.globalSettings = settingParser.parse(stream);
        this.files = from(globalSettings).transform(toFile());
    }

    public Iterable<File> getFiles() {
        return files;
    }

    public void run() {
        jsonRunner = newJsonRunnerWithSetting(from(globalSettings).filter(byEnv(this.env)).transform(toRunnerSetting()), port);
        jsonRunner.run();
    }

    private Predicate<? super GlobalSetting> byEnv(final Optional<String> env) {

        return new Predicate<GlobalSetting>() {
            @Override
            public boolean apply(GlobalSetting globalSetting) {
                return !env.isPresent() || env.get().equalsIgnoreCase(globalSetting.getEnv());

            }
        };
    }


    private Function<GlobalSetting, RunnerSetting> toRunnerSetting() {
        return new Function<GlobalSetting, RunnerSetting>() {
            @Override
            public RunnerSetting apply(GlobalSetting setting) {
                try {
                    return new RunnerSetting(new FileInputStream(setting.getInclude()), setting.getContext(), setting.getFileRoot());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void stop() {
        jsonRunner.stop();
    }

    private Function<? super GlobalSetting, File> toFile() {
        return new Function<GlobalSetting, File>() {
            @Override
            public File apply(GlobalSetting input) {
                return new File(input.getInclude());
            }
        };
    }
}
