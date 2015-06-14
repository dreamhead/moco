package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

public class GlobalSettingParser {
    private final CollectionReader reader = new CollectionReader();

    public ImmutableList<GlobalSetting> parse(final InputStream is) {
        return reader.read(is, GlobalSetting.class);
    }
}
