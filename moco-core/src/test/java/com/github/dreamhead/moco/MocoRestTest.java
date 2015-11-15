package com.github.dreamhead.moco;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static com.github.dreamhead.moco.Moco.toJson;
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
                MocoRest.get("1", toJson(resource1)),
                MocoRest.get("2", toJson(resource2))
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
                MocoRest.get("1", toJson(resource1)),
                MocoRest.get("2", toJson(resource2))
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
                MocoRest.get("1", toJson(resource1)),
                MocoRest.get("2", toJson(resource2))
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
                MocoRest.get("1", toJson(resource1)),
                MocoRest.get("2", toJson(resource2))
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
                MocoRest.get("1", eq(header(HttpHeaders.CONTENT_TYPE), "application/json"), toJson(resource1)),
                MocoRest.get("2", eq(header(HttpHeaders.CONTENT_TYPE), "application/json"), toJson(resource2))
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
