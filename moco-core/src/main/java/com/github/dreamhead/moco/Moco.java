package com.github.dreamhead.moco;

import com.github.dreamhead.moco.action.MocoAsyncAction;
import com.github.dreamhead.moco.action.MocoRequestAction;
import com.github.dreamhead.moco.config.MocoContextConfig;
import com.github.dreamhead.moco.config.MocoFileRootConfig;
import com.github.dreamhead.moco.extractor.*;
import com.github.dreamhead.moco.handler.*;
import com.github.dreamhead.moco.handler.failover.DefaultFailover;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.matcher.*;
import com.github.dreamhead.moco.monitor.verification.AtLeastVerification;import com.github.dreamhead.moco.monitor.DefaultRequestHit;
import com.github.dreamhead.moco.monitor.verification.AtMostVerification;import com.github.dreamhead.moco.monitor.verification.TimesVerification;
import com.github.dreamhead.moco.procedure.LatencyProcedure;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.github.dreamhead.moco.extractor.Extractors.extractor;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.github.dreamhead.moco.resource.ResourceFactory.*;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

public class Moco {
    private static final int DEFAULT_LATENCY = 1000;

    public static HttpServer httpserver(final int port, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createQuietServer(of(port), configs);
    }

    public static HttpServer httpserver(final int port, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(of(port), monitor, configs);
    }

    public static HttpServer httpserver(final MocoConfig... configs) {
        return ActualHttpServer.createQuietServer(Optional.<Integer>absent(), configs);
    }

    public static HttpServer httpserver(final MocoMonitor monitor, final MocoConfig... configs) {
        return ActualHttpServer.createHttpServerWithMonitor(Optional.<Integer>absent(), checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static MocoConfig context(final String context) {
        return new MocoContextConfig(checkNotNull(context, "Context should not be null"));
    }

    public static MocoConfig fileRoot(final String fileRoot) {
        return new MocoFileRootConfig(checkNotNull(fileRoot, "File root should not be null"));
    }

    public static RequestMatcher by(final String content) {
        return by(text(checkNotNull(content, "Content should not be null")));
    }

    public static RequestMatcher by(final Resource resource) {
        checkNotNull(resource, "resource should not be null");
        return eq(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final String expected) {
        return eq(checkNotNull(extractor, "Extractor should not be null"), text(checkNotNull(expected, "Expected content should not be null")));
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final Resource expected) {
        return new EqRequestMatcher<T>(checkNotNull(extractor, "Extractor should not be null"), checkNotNull(expected, "Expected content should not be null"));
    }

    public static RequestMatcher match(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return match(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final String expected) {
        return match(checkNotNull(extractor, "Extractor should not be null"), text(checkNotNull(expected, "Expected content should not be null")));
    }

    private static <T> RequestMatcher match(final RequestExtractor<T> extractor, final Resource expected) {
        return new MatchMatcher<T>(checkNotNull(extractor, "Extractor should not be null"),
                checkNotNull(expected, "Expected resource should not be null"));
    }

    public static RequestMatcher and(final RequestMatcher... matchers) {
        return new AndRequestMatcher(copyOf(matchers));
    }

    public static RequestMatcher or(final RequestMatcher... matchers) {
        return new OrRequestMatcher(copyOf(matchers));
    }

    public static ContentResource text(final String text) {
        return textResource(checkNotNull(text, "Text should not be null"));
    }

    public static ResponseHandler with(final String text) {
        return with(text(checkNotNull(text, "Text should not be null")));
    }

    public static ResponseHandler with(final Resource resource) {
        return responseHandler(checkNotNull(resource, "Resource should not be null"));
    }

    public static ResponseHandler with(final MocoProcedure procedure) {
        return new ProcedureResponseHandler(checkNotNull(procedure, "Procedure should not be null"));
    }

    public static Resource uri(final String uri) {
        return uriResource(checkNotNull(uri, "URI should not be null"));
    }

    public static Resource method(final String httpMethod) {
        return methodResource(checkNotNull(httpMethod, "HTTP method should not be null"));
    }

    public static RequestExtractor<String> header(final String header) {
        return new HeaderRequestExtractor(checkNotNull(header, "Header name should not be null"));
    }

    public static ResponseHandler header(final String name, final String value) {
        return header(checkNotNull(name, "Header name should not be null"), text(checkNotNull(value, "Header value should not be null")));
    }

    public static ResponseHandler header(final String name, final Resource value) {
        String key = checkNotNull(name, "Header name should not be null");
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Header name should not be empty");
        }

        return new HeaderResponseHandler(key,
                checkNotNull(value, "Header value should not be null"));
    }

    public static RequestExtractor<String> cookie(final String key) {
        return new CookieRequestExtractor(checkNotNull(key, "Cookie key should not be null"));
    }

    public static ResponseHandler cookie(final String key, final String value) {
        return cookie(checkNotNull(key, "Cookie key should not be null"), text(checkNotNull(value, "Cookie value should not be null")));
    }

    public static ResponseHandler cookie(final String key, final Resource resource) {
        return header("Set-Cookie", cookieResource(
                checkNotNull(key, "Cookie key should not be null"),
                checkNotNull(resource, "Cookie value should not be null")));
    }

    public static RequestExtractor<String> form(final String key) {
        return new FormRequestExtractor(checkNotNull(key, "Form key should not be null"));
    }

    public static LatencyProcedure latency(final long millis) {
        checkArgument(millis > 0, "Latency must be greater than zero");
        return new LatencyProcedure(millis);
    }

    public static RequestExtractor<String> query(final String param) {
        return new ParamRequestExtractor(checkNotNull(param, "Query parameter should not be null"));
    }

    public static XPathRequestExtractor xpath(final String xpath) {
        return new XPathRequestExtractor(checkNotNull(xpath, "XPath should not be null"));
    }

    public static RequestMatcher xml(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return new XmlRequestMatcher(extractor(resource.id()), resource);
    }

    public static RequestMatcher json(final Resource resource) {
        checkNotNull(resource, "JSON should not be null");
        return new JsonRequestMatcher(extractor(resource.id()), resource);
    }

    public static JsonPathRequestExtractor jsonPath(final String jsonPath) {
        return new JsonPathRequestExtractor(checkNotNull(jsonPath, "JsonPath should not be null"));
    }

    public static ResponseHandler seq(final String... contents) {
        return seq(from(copyOf(contents)).transform(textToResource()).toArray(ResponseHandler.class));
    }

    public static ResponseHandler seq(final Resource... contents) {
        return seq(from(copyOf(contents)).transform(resourceToResourceHandler()).toArray(ResponseHandler.class));
    }

    public static ResponseHandler seq(final ResponseHandler... handlers) {
        return new SequenceContentHandler(handlers);
    }

    public static ContentResource file(final String filename) {
        return fileResource(new File(checkNotNull(filename, "Filename should not be null")));
    }

    public static ContentResource pathResource(final String filename) {
        return classpathFileResource(checkNotNull(filename, "Filename should not be null"));
    }

    public static Resource version(final Resource resource) {
        return versionResource(checkNotNull(resource, "Version should not be null"));
    }

    public static Resource version(final String version) {
        return version(text(checkNotNull(version, "Version should not be null")));
    }

    public static ResponseHandler status(final int code) {
        return new StatusCodeResponseHandler(code);
    }

    public static ResponseHandler proxy(final String url) {
        return proxy(url, Failover.EMPTY_FAILOVER);
    }

    public static ResponseHandler proxy(final String url, Failover failover) {
        return new ProxyResponseHandler(toUrl(checkNotNull(url, "URL should not be null")), checkNotNull(failover, "Failover should not be null"));
    }

    public static Resource template(final String template) {
        return template(text(checkNotNull(template, "Template should not be null")));
    }

    public static Resource template(final ContentResource resource) {
        return templateResource(checkNotNull(resource, "Template should not be null"));
    }

    public static Failover failover(final String file) {
        return new DefaultFailover(new File(checkNotNull(file, "Filename should not be null")));
    }

    public static MocoEventTrigger complete(MocoEventAction action) {
        return new MocoEventTrigger(MocoEvent.COMPLETE, checkNotNull(action, "Action should not be null"));
    }

    public static MocoEventAction async(MocoEventAction action) {
        return async(checkNotNull(action, "Action should not be null"), latency(DEFAULT_LATENCY));
    }

    public static MocoEventAction async(MocoEventAction action, LatencyProcedure procedure) {
        return new MocoAsyncAction(checkNotNull(action, "Action should not be null"), checkNotNull(procedure, "Procedure should not be null"));
    }

    public static MocoEventAction get(String url) {
        return new MocoRequestAction(checkNotNull(url, "URL should not be null"), "GET", Optional.<ContentResource>absent());
    }

    public static MocoEventAction post(String url, ContentResource content) {
        return new MocoRequestAction(checkNotNull(url, "URL should not be null"), "POST", of(checkNotNull(content, "Content should not be null")));
    }

    public static MocoEventAction post(String url, String content) {
        return post(url, text(checkNotNull(content, "Content should not be null")));
    }

    public static RequestHit requestHit() {
        return new DefaultRequestHit();
    }

    public static UnexpectedRequestMatcher unexpected() {
        return new UnexpectedRequestMatcher() {};
    }

    public static VerificationMode never() {
        return times(0);
    }

    public static VerificationMode times(int count) {
        checkArgument(count >= 0, "Times count must not be less than zero");
        return new TimesVerification(count);
    }

    public static VerificationMode atLeast(int count) {
        checkArgument(count > 0, "Times count must be greater than zero");
        return new AtLeastVerification(count);
    }

    public static VerificationMode atMost(int count) {
        checkArgument(count > 0, "Times count must be greater than zero");
        return new AtMostVerification(count);
    }

    private static Function<String, ResponseHandler> textToResource() {
        return new Function<String, ResponseHandler>() {
            @Override
            public ResponseHandler apply(String content) {
                return with(text(content));
            }
        };
    }

    private static Function<Resource, ResponseHandler> resourceToResourceHandler() {
        return new Function<Resource, ResponseHandler>() {
            @Override
            public ResponseHandler apply(Resource content) {
                return with(content);
            }
        };
    }

    private static URL toUrl(String url) {
        try {
            return new URL(checkNotNull(url, "URL should not be null"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Moco() {
    }
}
