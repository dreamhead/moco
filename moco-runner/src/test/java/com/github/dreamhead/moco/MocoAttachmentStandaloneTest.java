package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoAttachmentStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_attach_text_attchment() throws IOException {
        runWithConfiguration("attachment.json");
        assertThat(helper.get(remoteUrl("/text_attachment")), is("text_attachment"));
    }

    @Test
    public void should_attach_file_attchment() throws IOException {
        runWithConfiguration("attachment.json");
        assertThat(helper.get(remoteUrl("/file_attachment")), is("foo.response"));
    }

    @Test
    public void should_attach_path_attchment() throws IOException {
        runWithConfiguration("attachment.json");
        assertThat(helper.get(remoteUrl("/path_attachment")), is("response from path"));
    }
}
