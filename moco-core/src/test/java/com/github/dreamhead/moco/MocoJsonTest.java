package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.support.JsonSupport;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MocoJsonTest extends AbstractMocoHttpTest {
    @Test
    public void should_return_content_based_on_jsonpath() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, () ->
                assertThat(helper.postContent(root(), "{\"book\":{\"price\":\"1\"}}"),
                        is("jsonpath match success")));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_mismatch_jsonpath() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, () -> helper.postContent(root(), "{\"book\":{\"price\":\"2\"}}"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_path_found() throws Exception {
        server.request(eq(jsonPath("anything"), "1")).response("jsonpath match success");
        running(server, () -> helper.postContent(root(), "{}"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_found() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, () -> helper.postContent(root(), "{}"));
    }

    @Test
    public void should_match_exact_json() throws Exception {
        final String jsonText = Jsons.toJson(of("foo", "bar"));
        server.request(by(json(jsonText))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), jsonText), is("foo")));
    }

    @Test
    public void should_match_exact_json_with_resource() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(by(json(jsonContent))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), jsonContent), is("foo")));
    }

    @Test
    public void should_match_same_structure_json() throws Exception {
        final String jsonText = Jsons.toJson(of("foo", "bar"));
        final String jsonText2 = Jsons.toJson(of("foo", "bar2"));
        server.request(as(json(jsonText))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), jsonText2), is("foo")));
    }

    @Test
    public void should_match_same_structure_json_with_resource() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(as(json(jsonContent))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), "{\"foo\":\"bar2\"}"), is("foo")));
    }


    @Test
    public void should_match_same_structure_POJO_json_resource() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";

        server.request(as(Moco.json(pojo))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), "{\n\t\"code\":2,\n\t\"message\":\"information\"\n}"), is("foo")));
    }

    @Test
    public void should_match_rule_json() throws Exception {
        final String ruleJsonText = Jsons.toJson("#value['foo'] == 'bar2'");
//        final String ruleJsonText = Jsons.toJson(of("foo", "#value == 'bar2'"));
        final String requestJsonText = Jsons.toJson(of("foo", "bar2"));
        server.request(rule(json(ruleJsonText))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), requestJsonText), is("foo")));
    }

    @Test
    public void should_match_rule_json_with_resource() throws Exception {

        final String ruleJsonText = "\"#value[name].equalsIgnoreCase('linus')||#value[name]=='Dreamhead'\"";
//        final String ruleJsonText = "{\"name\":\"#value.equalsIgnoreCase('linus')\"}";
        final String requestJsonText = "{\"name\":\"Linus\",\"location\":\"Portland\"}";
        final String responseText = "${req.json.name} is such a cool guy from ${req.json.location}.";
        server.request(rule(json(ruleJsonText))).response(template(responseText));
        running(server, () -> assertThat(helper.postContent(root(), requestJsonText), is("Linus is such a cool guy from Portland.")));
    }

    @Test
    public void should_match_POJO_json_resource() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(by(Moco.json(pojo))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), is("foo")));
    }

    @Test
    public void should_match_POJO_json() throws Exception {
        PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(by(Moco.json(pojo))).response("foo");
        running(server, () -> assertThat(helper.postContent(root(), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), is("foo")));
    }

    @Test
    public void should_return_content_based_on_jsonpath_existing() throws Exception {
        server.request(exist(jsonPath("$.book.price"))).response("jsonpath match success");
        running(server, () -> assertThat(helper.postContent(root(), "{\"book\":{\"price\":\"1\"}}"),
                is("jsonpath match success")));
    }

    @Test
    public void should_return_json_for_POJO() throws Exception {
        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.response(Moco.json(pojo));
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_return_json_for_text() throws Exception {
        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        String text = Jsons.toJson(pojo);
        server.response(Moco.json(text));
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_return_json_for_POJO_with_CJK() throws Exception {
        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "消息";
        server.response(Moco.json(pojo));
        running(server, () -> {
            String content = helper.get(root());
            JsonSupport.assertEquals(pojo, content);
        });
    }

    @Test
    public void should_match_request_with_gbk_resource() throws Exception {
        server = httpServer(port(), log());
        server.request(by(json(pathResource("gbk.json", Charset.forName("GBK"))))).response("response");

        running(server, () -> {
            String result = helper.postBytes(root(), "{\"message\": \"请求\"}".getBytes());
            assertThat(result, is("response"));
        });
    }

    @Test
    public void should_match_gbk_request() throws Exception {
        server = httpServer(port(), log());
        final Charset gbk = Charset.forName("GBK");
        server.request(by(json(pathResource("gbk.json", gbk)))).response("response");

        running(server, () -> {
            URL resource = Resources.getResource("gbk.json");
            byte[] bytes = ByteStreams.toByteArray(resource.openStream());
            String result = helper.postBytes(root(), bytes, gbk);
            assertThat(result, is("response"));
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

        running(server, () -> assertThat(helper.get(remoteUrl("/event")), is("event")));

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_return_json_dynamically() throws Exception {
        server.response(json((request) -> {
            PlainA pojo = new PlainA();
            pojo.code = 1;
            pojo.message = "message";
            return pojo;
        }));

        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_return_json_dynamically_with_text() throws Exception {
        server.response(json((request) -> {
            PlainA pojo = new PlainA();
            pojo.code = 1;
            pojo.message = "message";
            return Jsons.toJson(pojo);
        }));

        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_return_json_dynamically_with_resource() throws Exception {
        server.response(json((request) -> {
            PlainA pojo = new PlainA();
            pojo.code = 1;
            pojo.message = "message";
            return text(Jsons.toJson(pojo));
        }));

        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_return_json_dynamically_with_request() throws Exception {
        server.response(json((request) -> {
            PlainA pojo = new PlainA();
            pojo.code = 1;
            pojo.message = "message";
            return pojo;
        }));

        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_return_json_dynamically_with_stream() throws Exception {
        server.response(json((request) -> {
            PlainA pojo = new PlainA();
            pojo.code = 1;
            pojo.message = "message";
            String value = Jsons.toJson(pojo);
            return new ByteArrayInputStream(value.getBytes());
        }));

        final PlainA pojo = new PlainA();
        pojo.code = 1;
        pojo.message = "message";
        running(server, () -> JsonSupport.assertEquals(pojo, helper.getResponse(root())));
    }

    @Test
    public void should_throw_exception_for_null_dynamic_json() throws Exception {
        server.response(json((request) -> null));
        running(server, () -> {
            int status = helper.getForStatus(root());
            assertThat(status, is(400));
        });
    }

    private static class PlainA {
        public int code;
        public String message;
    }
}
