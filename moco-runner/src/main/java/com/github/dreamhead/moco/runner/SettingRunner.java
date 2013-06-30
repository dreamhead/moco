package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.parser.SettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithSetting;
import static com.google.common.collect.FluentIterable.from;

public class SettingRunner implements Runner {
    private final int port;
    private final List<GlobalSetting> globalSettings;
    private JsonRunner jsonRunner;
    private final FluentIterable<File> files;

    public SettingRunner(InputStream stream, int port) {
        this.port = port;
        this.globalSettings = new SettingParser().parse(stream);
        this.files = from(globalSettings).transform(toFile());
    }

    public Iterable<File> getFiles() {
        return files;
    }

    public void run() {
        jsonRunner = newJsonRunnerWithSetting(from(globalSettings).transform(toRunnerSetting()), port);
        jsonRunner.run();
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
