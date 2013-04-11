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

    public static RequestExtractor cookie(String key) {
        checkNotNull(key, "Null cookie is not allowed");
        return new CookieRequestExtractor(key);
    }

    public static ResponseHandler cookie(String key, String value) {
        checkNotNull(key, "Null cookie key is not allowed");
        checkNotNull(key, "Null cookie value is not allowed");
        return header("Set-Cookie", new Cookies().encodeCookie(key, value));
    }

    public static ResponseHandler latency(long millis) {
        return new LatencyResponseHandler(millis);
    }

    public static RequestExtractor query(String param) {
        checkNotNull(param, "Null query is not allowed");
        return new ParamRequestExtractor(param);
    }

    public static XPathRequestExtractor xpath(String xpath) {
        checkNotNull(xpath, "Null XPath is not allowed");
        return new XPathRequestExtractor(xpath);
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

    public static WritableResource file(String filename) {
        checkNotNull(filename, "Null filename is not allowed");

        return new FileResource(new File(filename));
    }

    public static ResponseHandler version(String version) {
        return new VersionResponseHandler(checkNotNull(version, "Null version is not allowed"));
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
