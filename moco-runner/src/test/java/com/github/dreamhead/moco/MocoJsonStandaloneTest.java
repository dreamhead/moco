package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response_based_on_specified_xml_request() throws IOException {
        runWithConfiguration("json.json");
        assertThat(helper.postContent(remoteUrl("/json"), "{\n\t\"foo\":\"bar\"\n}"), is("response_for_json_request"));
    }
    
    @Test
    public void should_return_expected_reponse_based_on_json_path_request() throws IOException {
    	runWithConfiguration("jsonpath.json");
    	assertThat(helper.postContent(remoteUrl("/jsonpath"), "{\"book\":[{\"price\":\"1\"}]}"), is("response_for_json_path_request"));
    }
}
