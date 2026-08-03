package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import com.github.dreamhead.moco.util.MediaType;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        ClassicHttpResponse response = helper.getResponse(url);
        HttpEntity entity = response.getEntity();
        byte[] bytes = entity.getContent().readAllBytes();
        assertThat(new String(bytes), is(content));
        MediaType mediaType = MediaType.parse(entity.getContentType());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
    }
}
