package com.github.dreamhead.moco;

import com.github.dreamhead.moco.runner.monitor.FileMocoRunnerMonitor;
import com.github.dreamhead.moco.util.Idles;
import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoEventStandaloneTest extends AbstractMocoStandaloneTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void should_fire_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/event")), is("foo"));
        Idles.idle(2000);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("success"));
    }
}
