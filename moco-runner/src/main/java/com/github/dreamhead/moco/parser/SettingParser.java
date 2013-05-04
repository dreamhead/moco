package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.parser.model.GlobalSetting;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SettingParser {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory factory = TypeFactory.defaultInstance();

    public List<GlobalSetting> parse(InputStream is) {
        try {
            return mapper.readValue(is, factory.constructCollectionType(List.class, GlobalSetting.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
