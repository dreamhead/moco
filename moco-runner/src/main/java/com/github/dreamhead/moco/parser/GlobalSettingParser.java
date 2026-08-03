package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.github.dreamhead.moco.util.Jsons;

import java.io.InputStream;
import java.util.List;

public final class GlobalSettingParser {
    public List<GlobalSetting> parse(final InputStream is) {
        return Jsons.toObjects(is, GlobalSetting.class);
    }
}
