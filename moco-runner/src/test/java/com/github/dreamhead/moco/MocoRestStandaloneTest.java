package com.github.dreamhead.moco;

import com.github.dreamhead.moco.util.Jsons;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRestStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_get_resource() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain = asPlain(response);

        assertThat(plain.code, is(1));
        assertThat(plain.message, is("foo"));

        Plain response2 = getResource("/targets/2");
        assertThat(response2.code, is(2));
        assertThat(response2.message, is("bar"));
    }

    @Test
    public void should_post() throws IOException {
        runWithConfiguration("rest/rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"),
                Jsons.toJson(resource1));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(201));
        assertThat(httpResponse.getFirstHeader("Location").getValue(), is("/targets/123"));
    }

    @Test
    public void should_put() throws IOException {
        runWithConfiguration("rest/rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"),
                Jsons.toJson(resource1));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void should_delete() throws IOException {
        runWithConfiguration("rest/rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.deleteForResponse(remoteUrl("/targets/1"));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void should_head() throws IOException {
        runWithConfiguration("rest/rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        org.apache.http.HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/1"));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
        assertThat(httpResponse.getHeaders("ETag")[0].getValue(), is("Moco"));
    }

    @Test
    public void should_patch() throws IOException {
        runWithConfiguration("rest/rest.json");

        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        assertThat(helper.patchForResponse(remoteUrl("/targets/1"), "result"), is("patch result"));
    }

    @Test
    public void should_get_resource_with_any_id() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/any-targets/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain = asPlain(response);

        assertThat(plain.code, is(1));
        assertThat(plain.message, is("any"));

        org.apache.http.HttpResponse response2 = helper.getResponseWithHeader(remoteUrl("/any-targets/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain2 = asPlain(response2);
        assertThat(plain2.code, is(1));
        assertThat(plain2.message, is("any"));
    }

    @Test
    public void should_get_resource_with_any_id_and_any_sub() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/any-targets/1/any-subs/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain = asPlain(response);

        assertThat(plain.code, is(100));
        assertThat(plain.message, is("any-sub"));

        org.apache.http.HttpResponse response2 = helper.getResponseWithHeader(remoteUrl("/any-targets/2/any-subs/2"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain2 = asPlain(response2);
        assertThat(plain2.code, is(100));
        assertThat(plain2.message, is("any-sub"));
    }

    @Test
    public void should_get_sub_resource() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1/subs/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain = asPlain(response);

        assertThat(plain.code, is(3));
        assertThat(plain.message, is("sub"));
    }

    @Test
    public void should_get_sub_sub_resource() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1/subs/1/sub-subs/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain = asPlain(response);

        assertThat(plain.code, is(4));
        assertThat(plain.message, is("sub-sub"));
    }

    @Test
    public void should_get_sub_resource_with_any_id() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1/any-subs/1"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Plain plain = asPlain(response);

        assertThat(plain.code, is(4));
        assertThat(plain.message, is("any-sub"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_no_response_found_in_rest_setting() {
        runWithConfiguration("rest/rest_error_without_response.json");
    }

    @Test
    public void should_get_all_resource() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/all-resources"),
                of(HttpHeaders.CONTENT_TYPE, "application/json"));

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        HttpEntity entity = response.getEntity();
        List<Plain> plains = Jsons.toObjects(entity.getContent(), Plain.class);
        assertThat(plains.size(), is(2));
    }

    @Test
    public void should_head_all_resource() throws IOException {
        runWithConfiguration("rest/rest.json");

        org.apache.http.HttpResponse httpResponse = helper.headForResponse(remoteUrl("/all-resources"));
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
        assertThat(httpResponse.getHeaders("ETag")[0].getValue(), is("Moco"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_without_put_id() {
        runWithConfiguration("rest/rest_error_without_put_id.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_without_delete_id() {
        runWithConfiguration("rest/rest_error_without_delete_id.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_without_patch_id() {
        runWithConfiguration("rest/rest_error_without_patch_id.json");
    }

    private Plain getResource(final String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));
        return asPlain(response);
    }

    private Plain asPlain(org.apache.http.HttpResponse response) throws IOException {
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        HttpEntity entity = response.getEntity();
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
        return Jsons.toObject(entity.getContent(), Plain.class);
    }

    private static class Plain {
        public int code;
        public String message;
    }
}
