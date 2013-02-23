package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class CookieResponseHandler implements ResponseHandler {
    private static final String COOKIE_HEADER = "Set-Cookie";

    private final String key;
    private final String value;

    public CookieResponseHandler(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        CookieEncoder cookieEncoder = new CookieEncoder(true);
        cookieEncoder.addCookie(key, value);
        response.setHeader(COOKIE_HEADER, cookieEncoder.encode());
    }
}
