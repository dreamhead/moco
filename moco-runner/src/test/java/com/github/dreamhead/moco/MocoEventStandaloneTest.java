package com.github.dreamhead.moco;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.util.Idles.idle;
import static com.google.common.io.Files.asCharSource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoEventStandaloneTest extends AbstractMocoStandaloneTest {
    private static final long IDLE = 1500;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void should_fire_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/event")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_get_event() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/get_event")), is("get_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_get_event_with_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/get_event_template")), is("get_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_unit() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/event-with-unit")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_url_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/post-event-with-template-url")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_content_template() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/post-event-with-template-content")), is("post_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XCAFEBABE"));
    }

    @Test
    public void should_fire_event_with_post_json() throws IOException {
        runWithConfiguration("event.json");
        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/event-with-json-post")), is("post_json_foo"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XMOCOJSON"));
    }

    @Test
    public void should_fire_event_with_get_for_header() throws IOException {
        runWithConfiguration("event.json");

        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/get_event_with_header")), is("get_foo_with_header"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XMOCOHEADER"));
    }

    @Test
    public void should_fire_event_with_post_for_header() throws IOException {
        runWithConfiguration("event.json");

        File file = folder.newFile();
        System.setOut(new PrintStream(file));
        assertThat(helper.get(remoteUrl("/post_event_with_header")), is("post_foo_with_header"));
        idle(IDLE, TimeUnit.MILLISECONDS);

        assertThat(asCharSource(file, Charset.defaultCharset()).read(), containsString("0XMOCOHEADER"));
    }
}
