package com.github.dreamhead.moco;

import com.github.dreamhead.moco.parser.GlobalSettingParser;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class MocoHttpsStandaloneTest {

    private GlobalSettingParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new GlobalSettingParser();
    }

    @Test
    public void should_parse_well() throws Exception {
        InputStream stream = getResourceAsStream("settings/https.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);

    }

    private InputStream getResourceAsStream(String filename) {
        return MocoHttpsStandaloneTest.class.getClassLoader().getResourceAsStream(filename);
    }
}
