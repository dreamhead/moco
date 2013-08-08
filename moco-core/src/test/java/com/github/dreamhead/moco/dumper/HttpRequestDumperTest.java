package com.github.dreamhead.moco.dumper;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestDumperTest {
    private FullHttpRequest request;

    @Before
    public void setup() {
        request = mock(FullHttpRequest.class);
    }

    @Test
    public void should_dump_http_request_as_specific_format() {
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getUri()).thenReturn("/get");
        when(request.getProtocolVersion()).thenReturn(HttpVersion.HTTP_1_0);
        when(request.headers()).thenReturn(DefaultHttpHeaders.EMPTY_HEADERS);

        HttpRequestDumper httpRequestDumper = new HttpRequestDumper();
        String content = httpRequestDumper.dump(request);
        assertThat(content,is("GET /get HTTP/1.0\r\n"));
    }
}

