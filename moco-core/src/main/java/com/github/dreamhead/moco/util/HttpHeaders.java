package com.github.dreamhead.moco.util;

public final class HttpHeaders {
    /*
     * Canonical-case header names, matching what Guava's HttpHeaders produced.
     *
     * The casing is load-bearing, so do NOT swap these for Netty's HttpHeaderNames, which are
     * lower-case. Header lookups in DefaultHttpMessage.getHeader and
     * DefaultMutableHttpResponse.getHeader are exact-key map lookups against names Netty parsed
     * off the wire, preserving the client's casing. HttpMessage.hasContent() depends on
     * CONTENT_LENGTH matching, and would silently start returning false.
     */
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String COOKIE = "Cookie";
    public static final String DATE = "Date";
    public static final String HOST = "Host";
    public static final String IF_MATCH = "If-Match";
    public static final String LOCATION = "Location";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String UPGRADE = "Upgrade";

    public static boolean isSameHeaderName(final String name, final String key) {
        return key.equalsIgnoreCase(name);
    }

    private HttpHeaders() {
    }
}
