package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.parser.model.GlobalSetting;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SettingParserTest {
    @Test
    public void should_parse_settings_file() {
        SettingParser parser = new SettingParser();
        InputStream stream = SettingParserTest.class.getClassLoader().getResourceAsStream("multiple/settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is("src/test/resources/multiple/foo.json"));
        assertThat(globalSettings.get(1).getInclude(), is("src/test/resources/multiple/bar.json"));
    }

    @Test
    public void should_parse_settings_file_with_context() {
        SettingParser parser = new SettingParser();
        InputStream stream = SettingParserTest.class.getClassLoader().getResourceAsStream("multiple/context-settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is("src/test/resources/multiple/foo.json"));
        assertThat(globalSettings.get(0).getContext(), is("/foo"));
        assertThat(globalSettings.get(1).getInclude(), is("src/test/resources/multiple/bar.json"));
        assertThat(globalSettings.get(1).getContext(), is("/bar"));
    }

    @Test
    public void should_parse_setting_file_with_file_root() {
        SettingParser parser = new SettingParser();
        InputStream stream = SettingParserTest.class.getClassLoader().getResourceAsStream("multiple/fileroot-settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is("src/test/resources/multiple/fileroot.json"));
        assertThat(globalSettings.get(0).getContext(), is("/fileroot"));
        assertThat(globalSettings.get(0).getFileRoot(), is("src/test/resources/"));
    }
}
