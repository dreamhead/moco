package com.github.dreamhead.moco;

import com.github.dreamhead.moco.config.MocoContextConfig;
import com.github.dreamhead.moco.config.MocoFileRootConfig;
import com.github.dreamhead.moco.extractor.*;
import com.github.dreamhead.moco.handler.*;
import com.github.dreamhead.moco.handler.failover.DefaultFailover;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.matcher.*;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.github.dreamhead.moco.extractor.Extractors.extractor;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.github.dreamhead.moco.resource.ResourceFactory.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

public class Moco {
    public static HttpServer httpserver(final int port, final MocoConfig... configs) {
        return new ActualHttpServer(port, configs);
    }

    public static MocoConfig context(final String context) {
        return new MocoContextConfig(checkNotNull(context, "context should not be null"));
    }

    public static MocoConfig fileRoot(final String fileRoot) {
        return new MocoFileRootConfig(checkNotNull(fileRoot, "file root should not be null"));
    }

    public static RequestMatcher by(final String content) {
        return by(text(content));
    }

    public static RequestMatcher by(final Resource resource) {
        return eq(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final String expected) {
        return eq(extractor, text(expected));
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final Resource expected) {
        return new EqRequestMatcher<T>(extractor, expected);
    }

    public static RequestMatcher match(final Resource patternResource) {
        return match(extractor(patternResource.id()), patternResource);
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final String expected) {
        return match(extractor, text(expected));
    }

    private static <T> RequestMatcher match(final RequestExtractor<T> extractor, final Resource expected) {
        return new MatchMatcher<T>(extractor, expected);
    }

    public static RequestMatcher and(final RequestMatcher... matchers) {
        return new AndRequestMatcher(copyOf(matchers));
    }

    public static RequestMatcher or(final RequestMatcher... matchers) {
        return new OrRequestMatcher(copyOf(matchers));
    }

    public static ContentResource text(final String text) {
        return textResource(checkNotNull(text, "Null text is not allowed"));
    }

    public static ResponseHandler with(String text) {
        return with(text(text));
    }

    public static ResponseHandler with(final Resource resource) {
        return responseHandler(resource);
    }

    public static Resource uri(final String uri) {
        return uriResource(checkNotNull(uri, "Null URI is not allowed"));
    }

    public static Resource method(final String httpMethod) {
        return methodResource(checkNotNull(httpMethod, "Null HTTP method is not allowed"));
    }

    public static RequestExtractor<String> header(final String header) {
        return new HeaderRequestExtractor(checkNotNull(header, "Null header name is not allowed"));
    }

    public static ResponseHandler header(final String name, final String value) {
        return header(name, text(value));
    }

    public static ResponseHandler header(final String name, final Resource value) {
        String key = checkNotNull(name, "Null header name is not allowed");
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Null header name is not allowed");
        }

        return new HeaderResponseHandler(key,
                checkNotNull(value, "Null header value is not allowed"));
    }

    public static RequestExtractor<String> cookie(final String key) {
        return new CookieRequestExtractor(checkNotNull(key, "Null cookie is not allowed"));
    }

    public static ResponseHandler cookie(final String key, final String value) {
        return cookie(key, text(value));
    }

    public static ResponseHandler cookie(final String key, final Resource resource) {
        return header("Set-Cookie", cookieResource(
                checkNotNull(key, "Null cookie key is not allowed"),
                checkNotNull(resource, "Null cookie value is not allowed")));
    }

    public static RequestExtractor<String> form(final String key) {
        return new FormRequestExtractor(checkNotNull(key, "Null form name is not allowed"));
    }

    public static ResponseHandler latency(final long millis) {
        return new LatencyResponseHandler(millis);
    }

    public static RequestExtractor<String> query(final String param) {
        return new ParamRequestExtractor(checkNotNull(param, "Null query is not allowed"));
    }

    public static XPathRequestExtractor xpath(final String xpath) {
        return new XPathRequestExtractor(checkNotNull(xpath, "Null XPath is not allowed"));
    }

    public static RequestMatcher xml(final Resource resource) {
        return new XmlRequestMatcher(extractor(resource.id()), resource);
    }

    public static RequestMatcher json(final Resource resource) {
        return new JsonRequestMatcher(extractor(resource.id()), resource);
    }
    
    public static JsonPathRequestExtractor jsonPath(final String jsonPath) {
    	return new JsonPathRequestExtractor(checkNotNull(jsonPath, "Null JsonPath is not allowed"));
    }

    public static ResponseHandler seq(final String... contents) {
        return seq(from(copyOf(contents)).transform(textToResource()).toArray(Resource.class));
    }

    private static Function<String, Resource> textToResource() {
        return new Function<String, Resource>() {
            @Override
            public Resource apply(String content) {
                return text(content);
            }
        };
    }

    public static ResponseHandler seq(final Resource... contents) {
        return new SequenceContentHandler(contents);
    }

    public static ContentResource file(final String filename) {
        return fileResource(new File(checkNotNull(filename, "Null filename is not allowed")));
    }

    public static ContentResource pathResource(final String filename) {
        return classpathFileResource(checkNotNull(filename, "Null filename is not allowed"));
    }

    public static Resource version(final Resource resource) {
        return versionResource(checkNotNull(resource, "Null version is not allowed"));
    }

    public static Resource version(final String version) {
        return version(text(version));
    }

    public static ResponseHandler status(final int code) {
        return new StatusCodeResponseHandler(code);
    }

    public static ResponseHandler proxy(final String url) {
        return proxy(url, Failover.EMPTY_FAILOVER);
    }

    public static ResponseHandler proxy(final String url, Failover failover) {
        return new ProxyResponseHandler(toUrl(url), checkNotNull(failover, "Null failover is not allowed"));
    }

    public static Resource template(final String template) {
        return template(text(checkNotNull(template, "Null template is not allowed")));
    }

    public static Resource template(final ContentResource resource) {
        return templateResource(checkNotNull(resource, "Null template is not allowed"));
    }

    public static Failover failover(final String file) {
        return new DefaultFailover(new File(file));
    }

    private static URL toUrl(String url) {
        try {
            return new URL(checkNotNull(url, "Null url is not allowed"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Moco() {}
}
