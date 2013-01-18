package com.github.dreamhead.moco;

import com.github.dreamhead.moco.extractor.*;
import com.github.dreamhead.moco.handler.SequenceResponseHandler;
import com.github.dreamhead.moco.handler.StatusCodeResponseHandler;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.OrRequestMatcher;
import com.github.dreamhead.moco.resource.*;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public class Moco {
    public static HttpServer httpserver(int port) {
        return new HttpServer(port);
    }

    public static RequestMatcher by(String content) {
        return by(text(content));
    }

    public static RequestMatcher by(Resource resource) {
        return eq(extractor(resource.id()), resource);
    }

    public static RequestMatcher eq(RequestExtractor extractor, String expected) {
        return new EqRequestMatcher(extractor, text(expected));
    }

    public static RequestMatcher eq(RequestExtractor extractor, Resource expected) {
        return new EqRequestMatcher(extractor, expected);
    }

    public static RequestMatcher and(RequestMatcher... matchers) {
        return new AndRequestMatcher(matchers);
    }

    public static RequestMatcher or(RequestMatcher... matchers) {
        return new OrRequestMatcher(matchers);
    }

    public static Resource text(String text) {
        checkNotNull(text, "Null text is not allowed");
        return new TextResource(text);
    }

    public static Resource uri(String uri) {
        checkNotNull(uri, "Null URI is not allowed");
        return new UriResource(uri);
    }

    public static Resource method(String requestMethod) {
        checkNotNull(requestMethod, "Null method is not allowed");
        return new MethodResource(requestMethod);
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
        List<Resource> streams = newArrayList();
        for (String content : contents) {
            streams.add(text(content));
        }

        return new SequenceResponseHandler(streams.toArray(new Resource[streams.size()]));
    }

    public static ResponseHandler seq(Resource... contents) {
        return new SequenceResponseHandler(contents);
    }

    public static Resource file(String filename) {
        checkNotNull(filename, "Null filename is not allowed");

        File file = new File(filename);
        if (!file.exists()) {
            throw new RuntimeException(format("File %s not found", filename));
        }

        return new FileResource(file);
    }

    public static ResponseHandler status(int code) {
        return new StatusCodeResponseHandler(code);
    }

    public static Resource url(String url) {
        checkNotNull(url, "Null url is not allowed");

        try {
            return new UrlResource(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Resource cache(Resource resource) {
        checkNotNull(resource, "Null resource is not allowed for cache");

        return new CacheResource(resource);
    }

    private static ImmutableMap<String, ? extends RequestExtractor> extractors = of(
            "file", new ContentRequestExtractor(),
            "text", new ContentRequestExtractor(),
            "uri", new UriRequestExtractor(),
            "method", new HttpMethodExtractor()
    );

    private static RequestExtractor extractor(String id) {
        RequestExtractor extractor = extractors.get(id);
        if (extractor == null) {
            throw new RuntimeException(format("unknown extractor for [%s]", id));
        }
        return extractor;
    }
}
