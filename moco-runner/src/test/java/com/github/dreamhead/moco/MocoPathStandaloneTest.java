package com.github.dreamhead.moco;

import org.apache.hc.client5.http.HttpResponseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MocoPathStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri_path() throws IOException {
        runWithConfiguration("path.json");
        assertThat(helper.get(remoteUrl("/path/path")), is("path"));
        assertThat(helper.get(remoteUrl("/path/path/sub/sub")), is("sub"));
    }

    @Test
    public void should_not_match_uri_path() {
        runWithConfiguration("path.json");
        assertThrows(HttpResponseException.class, () -> {
            helper.get(remoteUrl("/unknown/path"));
        });

    }
}
