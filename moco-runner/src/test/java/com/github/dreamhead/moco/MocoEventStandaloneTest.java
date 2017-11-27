package com.github.dreamhead.moco;

import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.util.Idles.idle;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoEventStandaloneTest extends AbstractMocoStandaloneTest {
    private static final long IDLE = 1200;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void should_fire_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/event")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_get_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/get_event")), is("get_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_get_event_with_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/get_event_template")), is("get_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_unit() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/event-with-unit")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_url_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/post-event-with-template-url")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_content_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/post-event-with-template-content")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_json() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        assertThat(helper.get(remoteUrl("/event-with-json-post")), is("post_json_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(Files.toString(file, Charset.defaultCharset()), containsString("0XMOCOJSON"));
    }
}
