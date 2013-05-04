package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.parser.SettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.base.Function;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

public class SettingRunner {
    private final SettingParser settingParser = new SettingParser();
    private final JsonRunner jsonRunner = new JsonRunner();

    public void run(InputStream stream, int port) {
        List<GlobalSetting> globalSettings = settingParser.parse(stream);
        jsonRunner.run(from(globalSettings).transform(toStream()).toList(), port);
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
