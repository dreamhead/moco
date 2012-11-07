package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.io.Resources;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoStandaloneTest {
    @BeforeClass
    public static void startStandaloneServer() throws IOException {
        String resourceName = "foo.json";
        InputStream is = Resources.getResource(resourceName).openStream();
        new JsonRunner().run(is);
    }

    @Test
    public void should_return_expected_response() throws IOException {
        assertThat(MocoTestHelper.get("http://localhost:8080"), is("foo"));
    }

    @Test
    public void should_return_expected_response_with_text_api() throws IOException {
        assertThat(MocoTestHelper.get("http://localhost:8080/foo"), is("bar"));
    }
}
