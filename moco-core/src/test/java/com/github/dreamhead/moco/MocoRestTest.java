package com.github.dreamhead.moco;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRestTest extends AbstractMocoHttpTest {
    @Test
    public void should_get_resource_by_id() throws Exception {
        Plain resource1 = new Plain();
        resource1.code = 1;
        resource1.message = "hello";

        Plain resource2 = new Plain();
        resource2.code = 2;
        resource2.message = "world";

        server.resource("target", of(
                "1", Moco.toJson(resource1),
                "2", Moco.toJson(resource2)
        ));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Plain response1 = getResource("/target/1");
                assertThat(response1.code, is(1));
                assertThat(response1.message, is("hello"));

                Plain response2 = getResource("/target/2");
                assertThat(response2.code, is(2));
                assertThat(response2.message, is("world"));
            }
        });
    }

    private Plain getResource(String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));
        HttpEntity entity = response.getEntity();
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
        String content = CharStreams.toString(new InputStreamReader(entity.getContent(), Charset.defaultCharset()));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, Plain.class);
    }

    private static class Plain {
        public int code;
        public String message;
    }
}
