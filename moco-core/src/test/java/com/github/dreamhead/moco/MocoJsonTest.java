package com.github.dreamhead.moco;

import com.github.dreamhead.moco.util.Jsons;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.json;
import static com.github.dreamhead.moco.Moco.jsonPath;
import static com.github.dreamhead.moco.Moco.toJson;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(json(jsonContent)).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonContent), is("foo"));
            }
        });
    }

    @Test
    public void should_match_exact_json_with_resource() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(json(jsonContent)).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonContent), is("foo"));
            }
        });
    }

    @Test
    public void should_match_same_structure_json() throws Exception {
        server.request(json("{\"foo\":\"bar\"}")).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\n\t\"foo\":\"bar\"\n}"), is("foo"));
            }
        });
    }

    @Test
    public void should_match_POJO_json() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(json(pojo)).response("foo");
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
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.response(toJson(pojo));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.HttpResponse response = helper.getResponse(root());
                HttpEntity entity = response.getEntity();
                MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
                assertThat(mediaType.type(), is("application"));
                assertThat(mediaType.subtype(), is("json"));
                PlainA responseA = Jsons.toObject(entity.getContent(), PlainA.class);
                assertThat(responseA.code, is(1));
                assertThat(responseA.message, is("message"));
            }
        });
    }

    @Test
    public void should_return_json_for_POJO_with_CJK() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "消息";
        server.response(toJson(pojo));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = helper.get(remoteUrl(root()));
                PlainA responseA = Jsons.toObject(content, PlainA.class);
                assertThat(responseA.code, is(1));
                assertThat(responseA.message, is("消息"));
            }
        });
    }

    private static class PlainA {
        public int code;
        public String message;
    }
}
