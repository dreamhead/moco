package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.HttpsCertificate;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitJsonHttpsRunnerWithPathTest extends AbstractMocoStandaloneTest {
    private final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonHttpsRunner(12306, pathResource("foo.json"), DEFAULT_CERTIFICATE);

    @Test
    public void should_return_expected_message() throws IOException {
        assertThat(helper.get(httpsRoot()), is("foo"));
    }

    @Test
    public void should_return_expected_message_2() throws IOException {
        assertThat(helper.get(httpsRoot()), is("foo"));
    }
}
