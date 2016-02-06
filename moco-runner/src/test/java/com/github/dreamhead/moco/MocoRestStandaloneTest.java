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

        Plain response1 = getResource("/targets/1");
        assertThat(response1.code, is(1));
        assertThat(response1.message, is("foo"));

        Plain response2 = getResource("/targets/2");
        assertThat(response2.code, is(2));
        assertThat(response2.message, is("bar"));
    }

    @Test
    public void should_post() throws IOException {
        runWithConfiguration("rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"),
                mapper.writeValueAsString(resource1));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(201));
        assertThat(httpResponse.getFirstHeader("Location").getValue(), is("/targets/123"));
    }

    @Test
    public void should_put() throws IOException {
        runWithConfiguration("rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"),
                mapper.writeValueAsString(resource1));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void should_delete() throws IOException {
        runWithConfiguration("rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.deleteForResponse(remoteUrl("/targets/1"));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void should_head() throws IOException {
        runWithConfiguration("rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/1"));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
        assertThat(httpResponse.getHeaders("ETag")[0].getValue(), is("Moco"));
    }

    @Test
    public void should_patch() throws IOException {
        runWithConfiguration("rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        assertThat(helper.patchForResponse(remoteUrl("/targets/1"), "result"), is("patch result"));
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
