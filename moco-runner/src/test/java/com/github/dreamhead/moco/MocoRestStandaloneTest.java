package com.github.dreamhead.moco;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRestStandaloneTest extends AbstractMocoStandaloneTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void should_get_resource() throws IOException {
        runWithConfiguration("rest.json");

        Plain response = getResource("/targets/1");
        assertThat(response.code, is(1));
        assertThat(response.message, is("foo"));
    }

    private Plain getResource(String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));
        return asPlain(response);
    }

    private Plain asPlain(org.apache.http.HttpResponse response) throws IOException {
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        HttpEntity entity = response.getEntity();
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
        return mapper.readValue(entity.getContent(), Plain.class);
    }

    private static class Plain {
        public int code;
        public String message;
    }
}
