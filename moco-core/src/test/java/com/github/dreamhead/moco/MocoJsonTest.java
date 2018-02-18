package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.support.JsonSupport;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.complete;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.json;
import static com.github.dreamhead.moco.Moco.jsonPath;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.post;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MocoJsonTest extends AbstractMocoHttpTest {
    @Test
    public void should_return_content_based_on_jsonpath() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\"book\":{\"price\":\"1\"}}"),
                        is("jsonpath match success"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_mismatch_jsonpath() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "{\"book\":{\"price\":\"2\"}}");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_path_found() throws Exception {
        server.request(eq(jsonPath("anything"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "{}");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_found() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "{}");
            }
        });
    }

    @Test
    public void should_match_exact_json() throws Exception {
        final String jsonText = Jsons.toJson(of("foo", "bar"));
        server.request(by(json(jsonText))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonText), is("foo"));
            }
        });
    }

    @Test
    public void should_match_exact_json_with_resource() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(by(json(jsonContent))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonContent), is("foo"));
            }
        });
    }

    @Test
    public void should_match_same_structure_json() throws Exception {
        final String jsonText = Jsons.toJson(of("foo", "bar"));
        server.request(by(json(jsonText))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonText), is("foo"));
            }
        });
    }

    @Test
    public void should_match_POJO_json_resource() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(by(Moco.json(pojo))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), is("foo"));
            }
        });
    }

    @Test
    public void should_match_POJO_json() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(by(Moco.json(pojo))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), is("foo"));
            }
        });
    }

    @Test
    public void should_return_content_based_on_jsonpath_existing() throws Exception {
        server.request(exist(jsonPath("$.book.price"))).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\"book\":{\"price\":\"1\"}}"),
                        is("jsonpath match success"));
            }
        });
    }

    @Test
    public void should_return_json_for_POJO() throws Exception {
        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.response(Moco.json(pojo));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                JsonSupport.assertEquals(pojo, helper.getResponse(root()));
            }
        });
    }

    @Test
    public void should_return_json_for_POJO_with_CJK() throws Exception {
        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "消息";
        server.response(Moco.json(pojo));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = helper.get(remoteUrl(root()));
                JsonSupport.assertEquals(pojo, content);
            }
        });
    }

    @Test
    public void should_match_request_with_gbk_resource() throws Exception {
        server = httpServer(port(), log());
        server.request(by(json(pathResource("gbk.json", Charset.forName("GBK"))))).response("response");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.postBytes(root(), "{\"message\": \"请求\"}".getBytes());
                assertThat(result, is("response"));
            }
        });
    }

    @Test
    public void should_match_gbk_request() throws Exception {
        server = httpServer(port(), log());
        final Charset gbk = Charset.forName("GBK");
        server.request(by(json(pathResource("gbk.json", gbk)))).response("response");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                URL resource = Resources.getResource("gbk.json");
                byte[] bytes = ByteStreams.toByteArray(resource.openStream());
                String result = helper.postBytes(root(), bytes, gbk);
                assertThat(result, is("response"));
            }
        });
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_json() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";

        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by(Moco.json(pojo)))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), Moco.json(pojo))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    private static class PlainA {
        public int code;
        public String message;
    }
}
