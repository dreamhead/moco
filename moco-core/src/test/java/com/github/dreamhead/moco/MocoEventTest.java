package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.util.Idles;
import com.google.common.collect.ImmutableMultimap;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.asHeader;
import static com.github.dreamhead.moco.Moco.async;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.complete;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.fileRoot;
import static com.github.dreamhead.moco.Moco.get;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.latency;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.post;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MocoEventTest extends AbstractMocoHttpTest {
    @Test
    public void should_fire_event_on_complete() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        server.response("foo").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foo"));
            }
        });

        verify(action).execute(any(Request.class));
    }

    @Test
    public void should_not_fire_event_on_specific_request() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        server.request(by(uri("/event"))).response("event").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(action).execute(any(Request.class));
    }

    @Test
    public void should_not_fire_event_on_other_request() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        server.request(by(uri("/noevent"))).response("noevent");
        server.request(by(uri("/event"))).response("foo").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/noevent")), is("noevent"));
            }
        });

        verify(action, Mockito.never()).execute(any(Request.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(remoteUrl("/target"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_with_text_url_and_header_to_target_on_complete_with_resource() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), eq(header("foo"), "bar"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(remoteUrl("/target"),
                asHeader("foo", text("bar")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_resource() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(text(remoteUrl("/target")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_with_header_to_target_on_complete_with_resource() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), eq(header("foo"), "bar"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(text(remoteUrl("/target")),
                asHeader("foo", text("bar")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_template_fetching_var_from_request() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(template("${base}/${req.headers['foo']}", "base", root()))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(remoteUrl("/event"), ImmutableMultimap.of("foo", "target")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_path_resource() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(pathResource("template.url"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }


    @Test
    public void should_send_get_request_to_target_on_complete_with_template() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(template("${var}", "var", remoteUrl("/target")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_string() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), "content")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_string_and_resource_url() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(text(remoteUrl("/target")), "content")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_path_resource_url() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(pathResource("template.url"), "content")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_path_resource_content() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), pathResource("foo.request"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), text("content"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_with_header_to_target_on_complete() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"), eq(header("foo"), "bar"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(text(remoteUrl("/target")), text("content"), asHeader("foo", text("bar")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_template_content() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), template("${req.headers['foo']}"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(remoteUrl("/event"), ImmutableMultimap.of("foo", "content")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_resource_url() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(text(remoteUrl("/target")), text("content"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_asyc() throws Exception {
        final ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(async(post(remoteUrl("/target"), text("content")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
                verify(handler, never()).writeToResponse(any(SessionContext.class));
                Idles.idle(2, TimeUnit.SECONDS);
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_async_after_awhile() throws Exception {
        final ResponseHandler handler = mock(ResponseHandler.class);
        server.request(and(by(uri("/target")), by("content"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(async(post(remoteUrl("/target"), text("content")), latency(1, TimeUnit.SECONDS))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
                verify(handler, never()).writeToResponse(any(SessionContext.class));
                Idles.idle(2, TimeUnit.SECONDS);
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_fire_event_for_context_configuration() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        when(action.apply(any(MocoConfig.class))).thenReturn(action);
        server = httpServer(port(), context("/context"));
        server.get(by(uri("/foo"))).response("foo").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/context/foo")), is("foo"));
            }
        });

        verify(action).execute(any(Request.class));
    }

    @Test
    public void should_send_post_request_with_file_root_configuration() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        when(handler.apply(any(MocoConfig.class))).thenReturn(handler);

        server = httpServer(port(), fileRoot("src/test/resources"));
        server.request(by(uri("/target")), by(file("foo.request"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), file("foo.request"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_gbk() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        final Charset gbk = Charset.forName("GBK");
        server.request(and(by(uri("/target")), by(pathResource("gbk.json", gbk)))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(text(remoteUrl("/target")), pathResource("gbk.json", gbk))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(any(SessionContext.class));
    }
}
