package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Header names are matched by exact map key against names Netty parsed off the wire, which keeps
 * the client's casing. Lower-casing these - for instance by reaching for Netty's HttpHeaderNames -
 * would make HttpMessage.hasContent() silently return false, so the canonical casing is pinned
 * here. Values captured from Guava 33.6.0-jre.
 */
public class HttpHeadersTest {
    @Test
    public void should_use_canonical_casing() {
        assertThat(HttpHeaders.CACHE_CONTROL, is("Cache-Control"));
        assertThat(HttpHeaders.CONNECTION, is("Connection"));
        assertThat(HttpHeaders.CONTENT_DISPOSITION, is("Content-Disposition"));
        assertThat(HttpHeaders.CONTENT_LENGTH, is("Content-Length"));
        assertThat(HttpHeaders.CONTENT_TYPE, is("Content-Type"));
        assertThat(HttpHeaders.COOKIE, is("Cookie"));
        assertThat(HttpHeaders.DATE, is("Date"));
        assertThat(HttpHeaders.HOST, is("Host"));
        assertThat(HttpHeaders.IF_MATCH, is("If-Match"));
        assertThat(HttpHeaders.LOCATION, is("Location"));
        assertThat(HttpHeaders.SERVER, is("Server"));
        assertThat(HttpHeaders.SET_COOKIE, is("Set-Cookie"));
        assertThat(HttpHeaders.UPGRADE, is("Upgrade"));
    }
}
