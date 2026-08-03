package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpRequestDumperTest {
    @Test
    public void should_dump_queries() {
        // Map.of randomises iteration order per JVM run, which would make this assertion flaky.
        Map<String, String[]> queries = new LinkedHashMap<>();
        queries.put("foo", new String[]{"fooValue"});
        queries.put("bar", new String[]{"barValue"});

        final DefaultHttpRequest request = DefaultHttpRequest.builder()
                .withUri("/hello")
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withMethod(HttpMethod.GET)
                .withQueries(queries)
                .build();

        HttpRequestDumper dumper = new HttpRequestDumper();
        final String result = dumper.dump(request);
        assertThat(result.trim(), is("GET /hello?foo=fooValue&bar=barValue HTTP/1.1"));
    }

    /**
     * Header and query maps must iterate in insertion order. Map.of and Map.copyOf randomise
     * iteration order once per JVM, which would make the dump above pass or fail depending on the
     * run, so those must never be used on these paths - see util.Maps.
     */
    @Test
    public void should_preserve_header_and_query_order() {
        Map<String, String[]> ordered = new LinkedHashMap<>();
        ordered.put("zebra", new String[]{"1"});
        ordered.put("apple", new String[]{"2"});
        ordered.put("mango", new String[]{"3"});

        final DefaultHttpRequest request = DefaultHttpRequest.builder()
                .withUri("/hello")
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withMethod(HttpMethod.GET)
                .withHeaders(ordered)
                .withQueries(ordered)
                .build();

        assertThat(List.copyOf(request.getHeaders().keySet()), is(List.of("zebra", "apple", "mango")));
        assertThat(List.copyOf(request.getQueries().keySet()), is(List.of("zebra", "apple", "mango")));
    }
}