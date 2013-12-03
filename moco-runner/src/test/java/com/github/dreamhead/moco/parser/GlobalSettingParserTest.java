package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.parser.model.GlobalSetting;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.util.Files.join;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GlobalSettingParserTest {

    private GlobalSettingParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new GlobalSettingParser();
    }

    @Test
    public void should_parse_settings_file() {
        InputStream stream = getResourceAsStream("multiple/settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is(join("src", "test", "resources", "multiple", "foo.json")));
        assertThat(globalSettings.get(1).getInclude(), is(join("src", "test", "resources", "multiple", "bar.json")));
    }

    @Test
    public void should_parse_settings_file_with_context() {
        InputStream stream = getResourceAsStream("multiple/context-settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is(join("src", "test", "resources", "multiple", "foo.json")));
        assertThat(globalSettings.get(0).getContext(), is("/foo"));
        assertThat(globalSettings.get(1).getInclude(), is(join("src", "test", "resources", "multiple", "bar.json")));
        assertThat(globalSettings.get(1).getContext(), is("/bar"));
    }

    @Test
    public void should_parse_setting_file_with_file_root() {
        InputStream stream = getResourceAsStream("multiple/fileroot-settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is(join("src", "test", "resources", "multiple", "fileroot.json")));
        assertThat(globalSettings.get(0).getContext(), is("/fileroot"));
        assertThat(globalSettings.get(0).getFileRoot(), is("src/test/resources"));
    }

    @Test
    public void should_parse_setting_file_with_env() {
        InputStream stream = getResourceAsStream("multiple/env-settings.json");
        List<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).getInclude(), is(join("src", "test", "resources", "multiple", "foo.json")));
        assertThat(globalSettings.get(0).getContext(), is("/foo"));
        assertThat(globalSettings.get(0).getEnv(), is("foo"));
        assertThat(globalSettings.get(1).getInclude(), is(join("src", "test", "resources", "multiple", "bar.json")));
        assertThat(globalSettings.get(1).getContext(), is("/bar"));
        assertThat(globalSettings.get(1).getEnv(), is("bar"));
    }

    private InputStream getResourceAsStream(String filename) {
        return GlobalSettingParserTest.class.getClassLoader().getResourceAsStream(filename);
    }
}
