package com.github.dreamhead.moco;

import com.github.dreamhead.moco.extractor.*;
import com.github.dreamhead.moco.handler.*;
import com.github.dreamhead.moco.matcher.*;
import com.github.dreamhead.moco.resource.*;
import com.github.dreamhead.moco.util.Cookies;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
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
        return eq(extractor, text(expected));
    }

    public static RequestMatcher eq(RequestExtractor extractor, Resource expected) {
        return new EqRequestMatcher(extractor, expected);
    }

    public static RequestMatcher match(Resource patternResource) {
        return match(extractor(patternResource.id()), patternResource);
    }

    public static RequestMatcher match(RequestExtractor extractor, String expected) {
        return match(extractor, text(expected));
    }

    public static RequestMatcher match(RequestExtractor extractor, Resource expected) {
        Pattern pattern = Pattern.compile(new String(expected.asByteArray()));
        return new MatchMatcher(extractor, pattern);
    }

    public static RequestMatcher and(RequestMatcher... matchers) {
        return new AndRequestMatcher(newArrayList(matchers));
    }

    public static RequestMatcher or(RequestMatcher... matchers) {
        return new OrRequestMatcher(matchers);
    }

    public static TextResource text(String text) {
        return new TextResource(checkNotNull(text, "Null text is not allowed"));
    }

    public static ResponseHandler content(ContentResource resource) {
        return new ContentHandler(resource);
    }

    public static Resource uri(String uri) {
        return new UriResource(checkNotNull(uri, "Null URI is not allowed"));
    }

    public static Resource method(String requestMethod) {
        return new MethodResource(checkNotNull(requestMethod, "Null method is not allowed"));
    }

    public static RequestExtractor header(String header) {
        return new HeaderRequestExtractor(checkNotNull(header, "Null header is not allowed"));
    }

    public static ResponseHandler header(String name, String value) {
        return new HeaderResponseHandler(
                checkNotNull(name, "Null header name is not allowed"),
                checkNotNull(value, "Null header value is not allowed"));
    }

    public static RequestExtractor cookie(String key) {
        return new CookieRequestExtractor(checkNotNull(key, "Null cookie is not allowed"));
    }

    public static ResponseHandler cookie(String key, String value) {
        return header("Set-Cookie", new Cookies().encodeCookie(
                checkNotNull(key, "Null cookie key is not allowed"),
                checkNotNull(value, "Null cookie value is not allowed")));
    }

    public static RequestExtractor form(String key) {
        return new FormRequestExtractor(checkNotNull(key, "Null form name is not allowed"));
    }

    public static ResponseHandler latency(long millis) {
        return new LatencyResponseHandler(millis);
    }

    public static RequestExtractor query(String param) {
        return new ParamRequestExtractor(checkNotNull(param, "Null query is not allowed"));
    }

    public static XPathRequestExtractor xpath(String xpath) {
        return new XPathRequestExtractor(checkNotNull(xpath, "Null XPath is not allowed"));
    }

    public static RequestMatcher xml(Resource resource) {
        return new XmlRequestMatcher(extractor(resource.id()), resource);
    }

    public static RequestMatcher json(Resource resource) {
        return new JsonRequestMatcher(extractor(resource.id()), resource);
    }

    public static ResponseHandler seq(String... contents) {
        List<Resource> resources = transform(newArrayList(contents), toResource());
        return new SequenceContentHandler(resources.toArray(new Resource[resources.size()]));
    }

    private static Function<String, Resource> toResource() {
        return new Function<String, Resource>() {
            @Override
            public Resource apply(String content) {
                return text(content);
            }
        };
    }

    public static ResponseHandler seq(Resource... contents) {
        return new SequenceContentHandler(contents);
    }

    public static FileResource file(String filename) {
        return new FileResource(new File(checkNotNull(filename, "Null filename is not allowed")));
    }

    public static ClasspathFileResource pathResource(String filename) {
        return new ClasspathFileResource(checkNotNull(filename, "Null filename is not allowed"));
    }

    public static VersionResource version(String version) {
        return new VersionResource(checkNotNull(version, "Null version is not allowed"));
    }

    public static ResponseHandler status(int code) {
        return new StatusCodeResponseHandler(code);
    }

    public static UrlResource url(String url) {
        try {
            return new UrlResource(new URL(checkNotNull(url, "Null url is not allowed")));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ImmutableMap<String, ? extends RequestExtractor> extractors = of(
            "file", new ContentRequestExtractor(),
            "text", new ContentRequestExtractor(),
            "uri", new UriRequestExtractor(),
            "method", new HttpMethodExtractor(),
            "version", new VersionExtractor()
    );

    private static RequestExtractor extractor(String id) {
        RequestExtractor extractor = extractors.get(id);
        if (extractor == null) {
            throw new RuntimeException(format("unknown extractor for [%s]", id));
        }

        return extractor;
    }
}
