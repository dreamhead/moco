package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

public final class GlobalSettingParser {
    public ImmutableList<GlobalSetting> parse(final InputStream is) {
        return Jsons.toObjects(is, GlobalSetting.class);
    }
}
