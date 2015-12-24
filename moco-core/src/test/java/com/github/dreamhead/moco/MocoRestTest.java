package com.github.dreamhead.moco;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.toJson;
import static com.github.dreamhead.moco.MocoRest.delete;
import static com.github.dreamhead.moco.MocoRest.get;
import static com.github.dreamhead.moco.MocoRest.head;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected RestServer createServer(int port) {
        return restServer(port);
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
                get("1", toJson(resource1)),
                get("2", toJson(resource2))
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
                get(toJson(ImmutableList.of(resource1, resource2)))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.HttpResponse response = helper.getResponse(remoteUrl("/targets"));
                assertThat(response.getStatusLine().getStatusCode(), is(200));
                HttpEntity entity = response.getEntity();
                List<Plain> plains = mapper.readValue(entity.getContent(), new TypeReference<List<Plain>>() {
                });
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
                get("1", toJson(resource1)),
                get("2", toJson(resource2))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.HttpResponse response = helper.getResponse(remoteUrl("/targets"));
                assertThat(response.getStatusLine().getStatusCode(), is(200));
                HttpEntity entity = response.getEntity();
                List<Plain> plains = mapper.readValue(entity.getContent(), new TypeReference<List<Plain>>() {
                });
                assertThat(plains.size(), is(2));
            }
        });
    }

    @Test
    public void should_reply_404_for_unknown_resource() throws Exception {
        server.resource("targets");

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
                get("1", toJson(resource1)),
                get("2", toJson(resource2))
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
                get("1", toJson(resource1)),
                get("2", toJson(resource2))
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
                get("1", eq(header(HttpHeaders.CONTENT_TYPE), "application/json"), toJson(resource1)),
                get("2", eq(header(HttpHeaders.CONTENT_TYPE), "application/json"), toJson(resource2))
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
                get(eq(query("foo"), "bar"), toJson(ImmutableList.of(resource1, resource2)))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                List<Plain> plains = mapper.readValue(helper.get(remoteUrl("/targets?foo=bar")), new TypeReference<List<Plain>>() {
                });
                assertThat(plains.size(), is(2));

                HttpResponse response = helper.getResponse(remoteUrl("/targets"));
                assertThat(response.getStatusLine().getStatusCode(), is(404));
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
                post(status(201), header("Location", "/targets/123"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"),
                        mapper.writeValueAsString(resource1));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(201));
                assertThat(httpResponse.getFirstHeader("Location").getValue(), is("/targets/123"));
            }
        });
    }

    @Test
    public void should_post_with_header() throws Exception {
        RestServer server = restServer(12306);
        final Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        server.resource("targets",
                post(eq(header(HttpHeaders.CONTENT_TYPE), "application/json"), status(201), header("Location", "/targets/123"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"),
                        mapper.writeValueAsString(resource1), "application/json");
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(201));
                assertThat(httpResponse.getFirstHeader("Location").getValue(), is("/targets/123"));

                HttpResponse badRequest = helper.postForResponse(remoteUrl("/targets"),
                        mapper.writeValueAsString(resource1));
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
                post(status(201), header("Location", "/targets/123"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets/1"),
                        mapper.writeValueAsString(resource1));
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
                put("1", status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"),
                        mapper.writeValueAsString(resource1));
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
                put("1", status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/2"),
                        mapper.writeValueAsString(resource1));
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
                put("1", status(409))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"),
                        mapper.writeValueAsString(resource1));
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
                put("1", eq(header(HttpHeaders.IF_MATCH), "moco"), status(200))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponseWithHeaders(remoteUrl("/targets/1"),
                        mapper.writeValueAsString(resource1), of(HttpHeaders.IF_MATCH, "moco"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    @Test
    public void should_not_delete_with_unknown_id() throws Exception {
        server.resource("targets",
                delete("1", status(200))
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
                delete("1", status(200))
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
                delete("1", eq(header(HttpHeaders.IF_MATCH), "moco"), status(200))
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
                delete("1", status(409))
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
    public void should_head_with_all() throws Exception {
        server.resource("targets",
                head(header("ETag", "Moco"))
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
                head("1", header("ETag", "Moco"))
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
                head("1", header("ETag", "Moco"))
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
                head("1", eq(query("name"), "foo"), header("ETag", "Moco"))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/1?name=foo"));
                assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
            }
        });
    }

    private Plain getResource(String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));
        return asPlain(response);
    }

    private Plain asPlain(HttpResponse response) throws IOException {
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
