package com.github.dreamhead.moco;

import com.github.dreamhead.moco.extractor.*;
import com.github.dreamhead.moco.handler.SequenceResponseHandler;
import com.github.dreamhead.moco.handler.StatusCodeResponseHandler;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.OrRequestMatcher;
import com.github.dreamhead.moco.model.ContentStream;
import com.github.dreamhead.moco.model.FileContentStream;
import com.github.dreamhead.moco.model.StringContentStream;
import com.github.dreamhead.moco.model.UrlContentStream;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class Moco {
    public static HttpServer httpserver(int port) {
        return new HttpServer(port);
    }

    public static RequestMatcher by(String content) {
        return by(text(content));
    }

    public static RequestMatcher by(ContentStream stream) {
        return eq(new ContentRequestExtractor(), new String(stream.asByteArray()));
    }

    public static RequestMatcher by(Expectation expectation) {
        return eq(expectation.getExtractor(), expectation.getExpected());
    }

    public static RequestMatcher eq(RequestExtractor extractor, String expected) {
        return new EqRequestMatcher(extractor, expected);
    }

    public static RequestMatcher and(RequestMatcher... matchers) {
        return new AndRequestMatcher(matchers);
    }

    public static RequestMatcher or(RequestMatcher... matchers) {
        return new OrRequestMatcher(matchers);
    }

    public static ContentStream text(String text) {
        checkNotNull(text, "Null text is not allowed");
        return new StringContentStream(text);
    }

    public static Expectation uri(String uri) {
        checkNotNull(uri, "Null URI is not allowed");
        return new Expectation(new UriRequestExtractor(), uri);
    }

    public static Expectation method(String requestMethod) {
        checkNotNull(requestMethod, "Null method is not allowed");
        return new Expectation(new HttpMethodExtractor(), requestMethod.toUpperCase());
    }

    public static RequestExtractor header(String header) {
        checkNotNull(header, "Null header is not allowed");
        return new HeaderRequestExtractor(header);
    }

    public static ResponseHandler header(String name, String value) {
        return new HeaderResponseHandler(name, value);

    }

    public static RequestExtractor query(String param) {
        checkNotNull(param, "Null query is not allowed");
        return new ParamRequestExtractor(param);
    }

    public static XPathRequestExtractor xpath(String xpath) {
        checkNotNull(xpath, "Null XPath is not allowed");
        return new XPathRequestExtractor(xpath);
    }

    public static ResponseHandler seq(String... contents) {
        List<ContentStream> streams = newArrayList();
        for (String content : contents) {
            streams.add(text(content));
        }

        return new SequenceResponseHandler(streams.toArray(new ContentStream[streams.size()]));
    }

    public static ResponseHandler seq(ContentStream... contents) {
        return new SequenceResponseHandler(contents);
    }

    public static ContentStream file(String filename) {
        return new FileContentStream(new File(filename));
    }

    public static ResponseHandler status(int code) {
        return new StatusCodeResponseHandler(code);
    }

    public static ContentStream url(String url) {
        try {
            return new UrlContentStream(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
