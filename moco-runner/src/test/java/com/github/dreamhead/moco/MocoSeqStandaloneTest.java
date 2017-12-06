package com.github.dreamhead.moco;

import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoSeqStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_work_well() throws IOException {
        runWithConfiguration("seq.json");
        assertThat(helper.get(remoteUrl("/seq")), is("foo"));
        assertThat(helper.get(remoteUrl("/seq")), is("bar"));
        assertThat(helper.get(remoteUrl("/seq")), is("bar"));
    }

    @Test
    public void should_work_well_with_json() throws IOException {
        runWithConfiguration("seq.json");

        assertJson(remoteUrl("/seq-json"), "{\"foo\":\"bar\"}");
        assertJson(remoteUrl("/seq-json"), "{\"hello\":\"world\"}");
        assertJson(remoteUrl("/seq-json"), "{\"hello\":\"world\"}");
    }

    private void assertJson(final String url, final String content) throws IOException {
        HttpResponse response = helper.getResponse(url);
        HttpEntity entity = response.getEntity();
        byte[] bytes = ByteStreams.toByteArray(entity.getContent());
        assertThat(new String(bytes), is(content));
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
    }
}
