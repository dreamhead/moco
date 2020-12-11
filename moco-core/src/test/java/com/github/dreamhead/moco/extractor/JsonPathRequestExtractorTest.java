package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonPathRequestExtractorTest {

    @Test
    public void should_extract_empty_content_as_absent() {
        JsonPathRequestExtractor unitUnderTest = new JsonPathRequestExtractor("$..account");
        HttpRequest request = DefaultHttpRequest.builder()
                .withStringContent("")
                .build();
        Optional<Object> result = unitUnderTest.extract(request);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void should_extract_array() {
        JsonPathRequestExtractor unitUnderTest = new JsonPathRequestExtractor("$.detail_item_list[*].number");
        HttpRequest request = DefaultHttpRequest.builder()
                .withStringContent("{\n" +
                        "    \"detail_item_list\":[\n" +
                        "    {\n" +
                        "        \"number\": 100\n" +
                        "     }]\n" +
                        "}" +
                        "\"number\": 100" +
                        "}]}")
                .build();
        Optional<Object> result = unitUnderTest.extract(request);
        assertThat(result.isPresent(), is(true));
        String[] texts = (String[]) result.get();
        assertThat(texts[0], is("100"));
    }
}
