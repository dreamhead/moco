package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpRequestDumperTest {
    @Test
    public void should_dump_queries() {
        final DefaultHttpRequest request = DefaultHttpRequest.builder()
                .withUri("/hello")
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withMethod(HttpMethod.GET)
                .withQueries(ImmutableMap.of("foo", new String[]{"fooValue"}, "bar", new String[]{"barValue"}))
                .build();

        HttpRequestDumper dumper = new HttpRequestDumper();
        final String result = dumper.dump(request);
        assertThat(result.trim(), is("GET /hello?foo=fooValue&bar=barValue HTTP/1.1"));
    }
}