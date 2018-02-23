package com.github.dreamhead.moco;

import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.json;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.MocoRequestHit.times;
import static com.github.dreamhead.moco.MocoRest.anyId;
import static com.github.dreamhead.moco.MocoRest.delete;
import static com.github.dreamhead.moco.MocoRest.get;
import static com.github.dreamhead.moco.MocoRest.head;
import static com.github.dreamhead.moco.MocoRest.id;
import static com.github.dreamhead.moco.MocoRest.patch;
import static com.github.dreamhead.moco.MocoRest.post;
import static com.github.dreamhead.moco.MocoRest.put;
import static com.github.dreamhead.moco.MocoRest.restServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRestTest extends BaseMocoHttpTest<RestServer> {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Override
    protected RestServer createServer(final int port) {
        return restServer(port, log());
    }

    @Test
    public void should_get_resource_by_id() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get("1").response(json(resource1)),
                get("2").response(json(resource2))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/targets/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/targets/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));
            }
        });
    }

    @Test
    public void should_get_all_resources() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get().response(json(ImmutableList.of(resource1, resource2)))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String uri = "/targets";
                List<Plain> plains = getResources(uri);
                assertThat(plains.size(), is(2));
            }
        });
    }

    @Test
    public void should_get_all_resources_by_default() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get("1").response(json(resource1)),
                get("2").response(json(resource2))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                List<Plain> plains = getResources("/targets");
                assertThat(plains.size(), is(2));
            }
        });
    }

    @Test
    public void should_reply_404_for_unknown_resource() throws Exception {
        server.resource("targets", get("2").response(with(text("hello"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.HttpResponse response = helper.getResponse(remoteUrl("/targets/1"));
                assertThat(response.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_request_server_by_moco_config() throws Exception {
        RestServer server = restServer(12306, context("/rest"), Moco.response(header("foo", "bar")));

        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get("1").response(json(resource1)),
                get("2").response(json(resource2))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/rest/targets/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/rest/targets/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));

                org.apache.http.HttpResponse response = helper.getResponse(remoteUrl("/rest/targets/1"));
                assertThat(response.getHeaders("foo")[0].getValue(), is("bar"));
            }
        });
    }

    @Test
    public void should_log_request_and_response() throws Exception {
        RestServer server = restServer(port(), log());

        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "0XCAFE";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "0XBABE";

        server.resource("targets",
                get("1").response(json(resource1)),
                get("2").response(json(resource2))
        );

        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/targets"));
            }
        });

        String actual = Files.toString(file, Charset.defaultCharset());
        assertThat(actual, containsString("0XCAFE"));
        assertThat(actual, containsString("0XBABE"));
    }

    @Test
    public void should_get_resource_by_id_with_request_config() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get("1").request(eq(header(HttpHeaders.CONTENT_TYPE), "application/json")).response(json(resource1)),
                get("2").request(eq(header(HttpHeaders.CONTENT_TYPE), "application/json")).response(json(resource2))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1"),
                        of(HttpHeaders.CONTENT_TYPE, "application/json"));
                Plain response1 = asPlain(response);
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                org.apache.http.HttpResponse otherResponse = helper.getResponseWithHeader(remoteUrl("/targets/2"),
                        of(HttpHeaders.CONTENT_TYPE, "application/json"));
                Plain response2 = asPlain(otherResponse);
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));

                org.apache.http.HttpResponse notFoundResponse = helper.getResponse(remoteUrl("/targets/1"));
                assertThat(notFoundResponse.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_query_with_condition() throws Exception {
        RestServer server = restServer(12306);
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get().request(eq(query("foo"), "bar")).response(json(ImmutableList.of(resource1, resource2)))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                List<Plain> plains = Jsons.toObjects(helper.get(remoteUrl("/targets?foo=bar")), Plain.class);
                assertThat(plains.size(), is(2));

                HttpResponse response = helper.getResponse(remoteUrl("/targets"));
                assertThat(response.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_get_resource_by_any_id() throws Exception {
        Plain resource = new Plain();
        resource.code = 1;
        resource.message = "hello";

        server.resource("targets",
                get(anyId()).response(json(resource))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/targets/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/targets/2");
                assertThat(response2.code, is(1));
                assertThat(response2.message, is("hello"));
            }
        });
    }

    @Test
    public void should_post() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                post().response(status(201), header("Location", "/targets/123"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(201));
                assertThat(httpResponse.getFirstHeader("Location").getValue(), is("/targets/123"));
            }
        });
    }

    @Test
    public void should_post_with_header() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource = new Plain();
        resource.code = 1;
        resource.message = "hello";

        server.resource("targets",
                post().request(eq(header(HttpHeaders.CONTENT_TYPE), "application/json"))
                        .response(status(201), header("Location", "/targets/123"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"),
                        Jsons.toJson(resource), "application/json");
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(201));
                assertThat(httpResponse.getFirstHeader("Location").getValue(), is("/targets/123"));

                HttpResponse badRequest = helper.postForResponse(remoteUrl("/targets"),
                        Jsons.toJson(resource));
                assertThat(badRequest.getStatusLine().getStatusCode(), is(400));
            }
        });
    }

    @Test
    public void should_return_404_for_post_with_id_by_default() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                post().response(status(201), header("Location", "/targets/123"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets/1"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_put() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                put("1").response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_not_put_with_unknown_id() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                put("1").response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/2"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_put_with_response_handler() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                put("1").response(status(409))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(409));
            }
        });
    }

    @Test
    public void should_put_with_matcher() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                put("1").request(eq(header(HttpHeaders.IF_MATCH), "moco")).response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponseWithHeaders(remoteUrl("/targets/1"),
                        Jsons.toJson(resource1), of(HttpHeaders.IF_MATCH, "moco"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_put_with_any_id() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                put(anyId()).response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse1 = helper.putForResponse(remoteUrl("/targets/1"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse1.getStatusLine().getStatusCode(), is(200));

                HttpResponse httpResponse2 = helper.putForResponse(remoteUrl("/targets/2"),
                        Jsons.toJson(resource1));
                assertThat(httpResponse2.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_not_delete_with_unknown_id() throws Exception {
        server.resource("targets",
                delete("1").response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponse(remoteUrl("/targets/2"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_delete() throws Exception {
        server.resource("targets",
                delete("1").response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponse(remoteUrl("/targets/1"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_delete_with_matcher() throws Exception {
        server.resource("targets",
                delete("1").request(eq(header(HttpHeaders.IF_MATCH), "moco")).response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponseWithHeaders(remoteUrl("/targets/1"),
                        ImmutableMultimap.of(HttpHeaders.IF_MATCH, "moco"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_delete_with_response() throws Exception {
        server.resource("targets",
                delete("1").response(status(409))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponse(remoteUrl("/targets/1"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(409));
            }
        });
    }

    @Test
    public void should_delete_with_any_id() throws Exception {
        server.resource("targets",
                delete(anyId()).response(status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse1 = helper.deleteForResponse(remoteUrl("/targets/1"));
                assertThat(httpResponse1.getStatusLine().getStatusCode(), is(200));

                HttpResponse httpResponse2 = helper.deleteForResponse(remoteUrl("/targets/2"));
                assertThat(httpResponse2.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_head_with_all() throws Exception {
        server.resource("targets",
                head().response(header("ETag", "Moco"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
                assertThat(httpResponse.getHeaders("ETag")[0].getValue(), is("Moco"));
            }
        });
    }

    @Test
    public void should_head() throws Exception {
        server.resource("targets",
                head("1").response(header("ETag", "Moco"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/1"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_not_head_with_unknown_id() throws Exception {
        server.resource("targets",
                head("1").response(header("ETag", "Moco"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/2"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(404));
            }
        });
    }

    @Test
    public void should_head_with_matcher() throws Exception {
        server.resource("targets",
                head("1").request(eq(query("name"), "foo")).response(header("ETag", "Moco"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/1?name=foo"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_head_with_any_id() throws Exception {
        server.resource("targets",
                head(anyId()).response(header("ETag", "Moco"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse1 = helper.headForResponse(remoteUrl("/targets/1"));
                assertThat(httpResponse1.getStatusLine().getStatusCode(), is(200));

                HttpResponse httpResponse2 = helper.headForResponse(remoteUrl("/targets/2"));
                assertThat(httpResponse2.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_patch() throws Exception {
        server.resource("targets",
                patch("1").response(with(text("patch result")))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.patchForResponse(remoteUrl("/targets/1"), "result"), is("patch result"));
            }
        });
    }

    @Test
    public void should_patch_with_any_id() throws Exception {
        server.resource("targets",
                patch(anyId()).response(with(text("patch result")))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.patchForResponse(remoteUrl("/targets/1"), "result"), is("patch result"));
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_get_id_with_slash() throws Exception {
        get("1/1").response(status(200));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_get_id_with_space() {
        get("1 1").response(status(200));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_resource_name_with_slash() {
        server.resource("hello/world", get().response("hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_resource_name_with_space() {
        server.resource("hello world", get().response("hello"));
    }

    @Test
    public void should_get_sub_resource() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                id("1").name("sub").settings(
                        get("1").response(json(resource1)),
                        get("2").response(json(resource2))
                )
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/targets/1/sub/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/targets/1/sub/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));
            }
        });
    }

    @Test
    public void should_get_sub_resource_with_any_id() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                id(anyId()).name("sub").settings(
                        get("1").response(json(resource1)),
                        get("2").response(json(resource2))
                )
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/targets/1/sub/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/targets/2/sub/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_invalid_sub_resource_name() {
        id(anyId()).name("hello world");
    }

    @Test
    public void should_work_with_other_http_configuration() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("targets",
                get("1").response(json(resource1)),
                get("2").response(json(resource2))
        );

        server.response("hello");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/targets/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/targets/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));

                assertThat(helper.get(remoteUrl("/hello")), is("hello"));
            }
        });
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        final RequestHit hit = requestHit();
        final RestServer server = restServer(port(), hit, log());
        server.resource("targets",
                get("1").response(json(resource1)),
                get("2").response(json(resource2))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/targets/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/targets/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));
            }
        });

        hit.verify(by(uri("/targets/1")), times(1));
        hit.verify(by(uri("/targets/2")), times(1));
    }

    private Plain getResource(String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));
        return asPlain(response);
    }

    private Plain asPlain(final HttpResponse response) throws IOException {
        HttpEntity entity = checkJsonResponse(response);
        return Jsons.toObject(entity.getContent(), Plain.class);
    }

    private List<Plain> getResources(final String uri) throws IOException {
        HttpResponse response = helper.getResponse(remoteUrl(uri));
        return asPlains(response);
    }

    private List<Plain> asPlains(final HttpResponse response) throws IOException {
        HttpEntity entity = checkJsonResponse(response);
        return Jsons.toObjects(entity.getContent(), Plain.class);
    }

    private HttpEntity checkJsonResponse(final HttpResponse response) {
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        HttpEntity entity = response.getEntity();
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
        return entity;
    }

    private static class Plain {
        public int code;
        public String message;
    }
}
