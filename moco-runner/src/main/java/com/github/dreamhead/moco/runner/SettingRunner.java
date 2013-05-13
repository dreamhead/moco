package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.parser.SettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.io.*;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

public class SettingRunner implements Runner {
    private final int port;
    private JsonRunner jsonRunner;
    private FluentIterable<File> files;

    public SettingRunner(InputStream stream, int port) {
        this.port = port;
        List<GlobalSetting> globalSettings = new SettingParser().parse(stream);
        this.files = from(globalSettings).transform(toFile());
    }

    public Iterable<File> getFiles() {
        return files;
    }

    public void run() {
        jsonRunner = new JsonRunner(files.transform(toStream()), port);
        jsonRunner.run();
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

    private Function<File, InputStream> toStream() {
        return new Function<File, InputStream>() {
            @Override
            public InputStream apply(File input) {
                try {
                    return new FileInputStream(input);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
