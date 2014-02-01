package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamhead.moco.parser.deserializer.TextContainerDeserializer;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

public class GlobalSettingParser {
    private CollectionReader reader = new CollectionReader();

    public GlobalSettingParser() {
        Module textContainerModule = new SimpleModule("TextContainerModule",
                new Version(1, 0, 0, null, null, null))
                .addDeserializer(TextContainer.class, new TextContainerDeserializer());
        this.reader = new CollectionReader(textContainerModule);
    }

    public ImmutableList<GlobalSetting> parse(InputStream is) {
        return reader.read(is, GlobalSetting.class);
    }
}
