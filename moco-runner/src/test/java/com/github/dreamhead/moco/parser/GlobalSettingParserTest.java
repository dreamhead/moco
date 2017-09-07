package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

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
        InputStream stream = getResourceAsStream("settings/settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).includes().get(0), is(join("src", "test", "resources", "settings", "details", "foo.json")));
        assertThat(globalSettings.get(1).includes().get(0), is(join("src", "test", "resources", "settings", "details", "bar.json")));
    }

    @Test
    public void should_parse_settings_file_with_context() {
        InputStream stream = getResourceAsStream("settings/context-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).includes().get(0), is(join("src", "test", "resources", "settings", "details", "foo.json")));
        assertThat(globalSettings.get(0).getContext(), is("/foo"));
        assertThat(globalSettings.get(1).includes().get(0), is(join("src", "test", "resources", "settings", "details", "bar.json")));
        assertThat(globalSettings.get(1).getContext(), is("/bar"));
    }

    @Test
    public void should_parse_setting_file_with_file_root() {
        InputStream stream = getResourceAsStream("settings/fileroot-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).includes().get(0), is(join("src", "test", "resources", "settings", "fileroot.json")));
        assertThat(globalSettings.get(0).getContext(), is("/fileroot"));
        assertThat(globalSettings.get(0).getFileRoot(), is("src/test/resources"));
    }

    @Test
    public void should_parse_setting_file_with_env() {
        InputStream stream = getResourceAsStream("settings/env-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);

        assertThat(globalSettings.get(0).includes().get(0), is(join("src", "test", "resources", "settings", "details", "foo.json")));
        assertThat(globalSettings.get(0).getContext(), is("/foo"));
        assertThat(globalSettings.get(0).getEnv(), is("foo"));
        assertThat(globalSettings.get(1).includes().get(0), is(join("src", "test", "resources", "settings", "details", "bar.json")));
        assertThat(globalSettings.get(1).getContext(), is("/bar"));
        assertThat(globalSettings.get(1).getEnv(), is("bar"));
    }

    @Test
    public void should_parse_glob_settings_file() {
        InputStream stream = getResourceAsStream("settings/glob-settings.json");
        ImmutableList<GlobalSetting> globalSettings = parser.parse(stream);

        ImmutableList<String> includes = globalSettings.get(0).includes();
        assertThat(includes.contains(join("src", "test", "resources", "settings", "details", "foo.json")), is(true));
        assertThat(includes.contains(join("src", "test", "resources", "settings", "details", "bar.json")), is(true));
    }

    private InputStream getResourceAsStream(final String filename) {
        try {
            return Resources.getResource(filename).openStream();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
