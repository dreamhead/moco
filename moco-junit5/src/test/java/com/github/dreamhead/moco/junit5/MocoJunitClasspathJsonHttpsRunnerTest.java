package com.github.dreamhead.moco.junit5;

import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MocoJunit5Extension.class)
@MocoConfiguration(filepath = "src/test/resources/foo.json", port=12306)
@MocoCertificate(classpath = "certificate/cert.jks", keyStorePassword = "mocohttps", certPassword = "mocohttps")
public class MocoJunitClasspathJsonHttpsRunnerTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_message() throws IOException {
        assertThat(helper.get(httpsRoot()), is("foo"));
    }

    @Test
    public void should_return_expected_message_2() throws IOException {
        assertThat(helper.get(httpsRoot()), is("foo"));
    }
}
