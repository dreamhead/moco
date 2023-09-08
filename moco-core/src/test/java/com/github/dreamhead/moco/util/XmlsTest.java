package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XmlsTest {
    @Test
    public void should_convert_xml_to_object() {
        final String xml = "<request><parameter><id>1</id></parameter></request>";
        final TestRequest request = Xmls.toObject(xml, TestRequest.class);
        assertThat(request.getParameters().getId(), is("1"));
    }

    public static class TestRequest {
        private TestParameter parameter;

        public TestParameter getParameters() {
            return parameter;
        }
    }

    public static class TestParameter {
        private String id;

        public String getId() {
            return id;
        }
    }
}