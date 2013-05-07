package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.parser.SettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.base.Function;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

public class SettingRunner implements Runner {
    private final SettingParser settingParser = new SettingParser();
    private final InputStream stream;
    private final int port;
    private JsonRunner jsonRunner;

    public SettingRunner(InputStream stream, int port) {
        this.stream = stream;
        this.port = port;
    }

    public void run() {
        List<GlobalSetting> globalSettings = settingParser.parse(stream);
        jsonRunner = new JsonRunner(from(globalSettings).transform(toStream()).toList(), port);
        jsonRunner.run();
    }

    public void stop() {
        jsonRunner.stop();
    }

    private Function<GlobalSetting, InputStream> toStream() {
        return new Function<GlobalSetting, InputStream>() {
            @Override
            public InputStream apply(GlobalSetting input) {
                try {
                    return new FileInputStream(input.getInclude());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
