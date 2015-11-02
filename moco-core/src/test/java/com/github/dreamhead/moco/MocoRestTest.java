package com.github.dreamhead.moco;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.github.dreamhead.moco.MocoRest.restServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRestTest extends BaseMocoHttpTest<RestServer> {

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
                MocoRest.get("1", Moco.toJson(resource1)),
                MocoRest.get("2", Moco.toJson(resource2))
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
                MocoRest.get("1", Moco.toJson(resource1)),
                MocoRest.get("2", Moco.toJson(resource2))
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

    private Plain getResource(String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));
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
