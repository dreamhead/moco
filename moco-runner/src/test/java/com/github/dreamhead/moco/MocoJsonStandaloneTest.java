package com.github.dreamhead.moco;

import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response_based_on_specified_json_request() throws IOException {
        runWithConfiguration("json.json");
        assertThat(helper.postContent(remoteUrl("/json"), "{\n\t\"foo\":\"bar\"\n}"), is("response_for_json_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_json_request_shortcut() throws IOException {
        runWithConfiguration("json.json");
        assertThat(helper.postContent(remoteUrl("/json_shortcut"), "{\n\t\"foo\":\"bar\"\n}"), is("response_for_json_shortcut"));
    }

    @Test
    public void should_return_expected_json_response_based_on_specified_json_request_shortcut() throws IOException {
        runWithConfiguration("json.json");
        HttpResponse response = helper.getResponse(remoteUrl("/json_response_shortcut"));
        HttpEntity entity = response.getEntity();
        byte[] bytes = ByteStreams.toByteArray(entity.getContent());
        assertThat(new String(bytes), is("{\"foo\":\"bar\"}"));
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));
    }
    
    @Test
    public void should_return_expected_reponse_based_on_json_path_request() throws IOException {
    	runWithConfiguration("jsonpath.json");
    	assertThat(helper.postContent(remoteUrl("/jsonpath"), "{\"book\":{\"price\":\"1\"}}"), is("response_for_json_path_request"));
    }
}
