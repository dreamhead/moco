package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoXmlStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response_based_on_specified_xpath_request() throws IOException {
        runWithConfiguration("xpath.json");
        assertThat(helper.postFile(remoteUrl("/xpath"), "foo.xml"), is("response_for_xpath_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_xpath_request() throws IOException {
        runWithConfiguration("xpath.json");
        helper.postFile(remoteUrl("/xpath"), "bar.xml");
    }

    @Test
    public void should_return_expected_response_based_on_specified_xml_request() throws IOException {
        runWithConfiguration("xml.json");
        assertThat(helper.postFile(remoteUrl("/xml"), "foo.xml"), is("response_for_xml_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_xml_file_request() throws IOException {
        runWithConfiguration("xml.json");
        assertThat(helper.postFile(remoteUrl("/xmlfile"), "foo.xml"), is("response_for_xml_file_request"));
    }

    @Test
    public void should_return_expected_response_based_on_struct_xml() throws IOException {
        runWithConfiguration("xml_struct.json");
        assertThat(helper.postFile(remoteUrl("/struct-xml"), "foo.xml"), is("response_for_xml_struct_request"));
        assertThat(helper.postContent(remoteUrl("/struct-xml"), "<request><parameters><id>2</id></parameters></request>"), is("response_for_xml_struct_request"));
    }
}
