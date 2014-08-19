package com.github.dreamhead.moco;

import com.github.dreamhead.moco.action.MocoAsyncAction;
import com.github.dreamhead.moco.action.MocoRequestAction;
import com.github.dreamhead.moco.config.MocoContextConfig;
import com.github.dreamhead.moco.config.MocoFileRootConfig;
import com.github.dreamhead.moco.config.MocoResponseConfig;
import com.github.dreamhead.moco.extractor.*;
import com.github.dreamhead.moco.handler.*;
import com.github.dreamhead.moco.handler.failover.DefaultFailoverExecutor;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.failover.FailoverStrategy;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.matcher.*;
import com.github.dreamhead.moco.monitor.*;
import com.github.dreamhead.moco.procedure.LatencyProcedure;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.IdFactory;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.ResourceConfigApplierFactory;
import com.github.dreamhead.moco.resource.reader.CustomResourceReader;
import com.github.dreamhead.moco.util.URLs;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.HttpHeaders;

import java.io.File;

import static com.github.dreamhead.moco.extractor.Extractors.extractor;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.github.dreamhead.moco.resource.ResourceFactory.*;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

public class Moco {
    public static HttpServer httpserver(final int port, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createQuietServer(of(port), configs);
    }

    public static HttpServer httpserver(final int port, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(of(port),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpServer httpserver(final int port, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(of(port), mergeMonitor(monitor, monitor2, monitors));
    }

    private static MocoMonitor mergeMonitor(MocoMonitor monitor, MocoMonitor monitor2, MocoMonitor[] monitors) {
        MocoMonitor[] targetMonitors = new MocoMonitor[2 + monitors.length];
        targetMonitors[0] = checkNotNull(monitor, "Monitor should not be null");
        targetMonitors[1] = checkNotNull(monitor2, "Monitor should not be null");
        if (monitors.length > 0) {
            System.arraycopy(monitors, 0, targetMonitors, 2, monitors.length);
        }

        return new CompositeMonitor(targetMonitors);
    }

    public static HttpServer httpserver(final MocoConfig... configs) {
        return ActualHttpServer.createQuietServer(Optional.<Integer>absent(), configs);
    }

    public static HttpServer httpserver(final MocoMonitor monitor, final MocoConfig... configs) {
        return ActualHttpServer.createHttpServerWithMonitor(Optional.<Integer>absent(), checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpsServer httpsServer(final int port, final HttpsCertificate certificate, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsQuietServer(of(port), checkNotNull(certificate, "Certificate should not be null"), configs);
    }

    public static HttpsServer httpsServer(final int port, final HttpsCertificate certificate, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsServerWithMonitor(of(port),
                checkNotNull(certificate, "Certificate should not be null"),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpsServer httpsServer(final HttpsCertificate certificate, final MocoConfig... configs) {
        return ActualHttpServer.createHttpsQuietServer(Optional.<Integer>absent(), checkNotNull(certificate, "Certificate should not be null"), configs);
    }

    public static HttpsServer httpsServer(final HttpsCertificate certificate, final MocoMonitor monitor, final MocoConfig... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(Optional.<Integer>absent(),
                checkNotNull(certificate, "Certificate should not be null"),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpServer httpsServer(final int port, final HttpsCertificate certificate, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsServerWithMonitor(of(port), checkNotNull(certificate, "Certificate should not be null"),
                mergeMonitor(monitor, monitor2, monitors));
    }

    public static MocoConfig context(final String context) {
        return new MocoContextConfig(checkNotNullOrEmpty(context, "Context should not be null"));
    }

    public static MocoConfig response(final ResponseHandler handler) {
        return new MocoResponseConfig(checkNotNull(handler, "Response should not be null"));
    }

    public static MocoConfig fileRoot(final String fileRoot) {
        return new MocoFileRootConfig(checkNotNullOrEmpty(fileRoot, "File root should not be null"));
    }

    public static MocoMonitor log() {
        return new LogMonitor(new DefaultLogFormatter(), new StdLogWriter());
    }

    public static MocoMonitor log(final String filename) {
        return new LogMonitor(new DefaultLogFormatter(), new FileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty")));
    }

    public static RequestMatcher by(final String content) {
        return by(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static RequestMatcher by(final Resource resource) {
        checkNotNull(resource, "resource should not be null");
        return eq(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final String expected) {
        return eq(checkNotNull(extractor, "Extractor should not be null"), text(checkNotNullOrEmpty(expected, "Expected content should not be null")));
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final Resource expected) {
        return new EqRequestMatcher<T>(checkNotNull(extractor, "Extractor should not be null"), checkNotNull(expected, "Expected content should not be null"));
    }

    public static RequestMatcher match(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return match(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final String expected) {
        return match(checkNotNull(extractor, "Extractor should not be null"), text(checkNotNullOrEmpty(expected, "Expected content should not be null")));
    }

    private static <T> RequestMatcher match(final RequestExtractor<T> extractor, final Resource expected) {
        return new MatchMatcher<T>(checkNotNull(extractor, "Extractor should not be null"),
                checkNotNull(expected, "Expected resource should not be null"));
    }

    public static <T> RequestMatcher exist(final RequestExtractor<T> extractor) {
        return new ExistMatcher<T>(checkNotNull(extractor, "Extractor should not be null"));
    }

    public static RequestMatcher startsWith(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return startsWith(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher startsWith(RequestExtractor<T> extractor, String expected) {
        return startsWith(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    private static <T> RequestMatcher startsWith(RequestExtractor<T> extractor, Resource resource) {
        return new StartsWithMatcher<T>(checkNotNull(extractor, "Extractor should not be null"),
                checkNotNull(resource, "Expected resource should not be null"));
    }

    public static RequestMatcher endsWith(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return endsWith(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher endsWith(final RequestExtractor<T> extractor, final String expected) {
        return endsWith(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    private static <T> RequestMatcher endsWith(final RequestExtractor<T> extractor, final Resource resource) {
        return new EndsWithMatcher<T>(checkNotNull(extractor, "Extractor should not be null"),
                checkNotNull(resource, "Expected resource should not be null"));
    }

    public static RequestMatcher contain(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return contain(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher contain(final RequestExtractor<T> extractor, final String expected) {
        return contain(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    private static <T> RequestMatcher contain(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<T>(checkNotNull(extractor, "Extractor should not be null"),
                checkNotNull(resource, "Expected resource should not be null"));
    }

    public static RequestMatcher and(final RequestMatcher... matchers) {
        return new AndRequestMatcher(copyOf(matchers));
    }

    public static RequestMatcher or(final RequestMatcher... matchers) {
        return new OrRequestMatcher(copyOf(matchers));
    }

    public static RequestMatcher not(final RequestMatcher matcher) {
        return new NotRequestMatcher(checkNotNull(matcher, "Expected matcher should not be null"));
    }

    public static ContentResource text(final String text) {
        return textResource(checkNotNullOrEmpty(text, "Text should not be null"));
    }

    public static ResponseHandler with(final String text) {
        return with(text(checkNotNullOrEmpty(text, "Text should not be null")));
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
        return methodResource(checkNotNullOrEmpty(httpMethod, "HTTP method should not be null"));
    }

    public static RequestExtractor<String> header(final String header) {
        return new HeaderRequestExtractor(checkNotNullOrEmpty(header, "Header name should not be null"));
    }

    public static ResponseHandler header(final String name, final String value) {
        return header(checkNotNullOrEmpty(name, "Header name should not be null"), text(checkNotNullOrEmpty(value, "Header value should not be null")));
    }

    public static ResponseHandler header(final String name, final Resource value) {
        return new HeaderResponseHandler(checkNotNullOrEmpty(name, "Header name should not be null"),
                checkNotNull(value, "Header value should not be null"));
    }

    public static RequestExtractor<String> cookie(final String key) {
        return new CookieRequestExtractor(checkNotNullOrEmpty(key, "Cookie key should not be null"));
    }

    public static ResponseHandler cookie(final String key, final String value) {
        return cookie(checkNotNullOrEmpty(key, "Cookie key should not be null"), text(checkNotNullOrEmpty(value, "Cookie value should not be null")));
    }

    public static ResponseHandler cookie(final String key, final Resource resource) {
        return header(HttpHeaders.Names.SET_COOKIE, cookieResource(
                checkNotNullOrEmpty(key, "Cookie key should not be null"),
                checkNotNull(resource, "Cookie value should not be null")));
    }

    public static RequestExtractor<String> form(final String key) {
        return new FormRequestExtractor(checkNotNullOrEmpty(key, "Form key should not be null"));
    }

    public static LatencyProcedure latency(final long millis) {
        checkArgument(millis > 0, "Latency must be greater than zero");
        return new LatencyProcedure(millis);
    }

    public static RequestExtractor<String> query(final String param) {
        return new ParamRequestExtractor(checkNotNullOrEmpty(param, "Query parameter should not be null"));
    }

    public static XPathRequestExtractor xpath(final String xpath) {
        return new XPathRequestExtractor(checkNotNullOrEmpty(xpath, "XPath should not be null"));
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
        return new JsonPathRequestExtractor(checkNotNullOrEmpty(jsonPath, "JsonPath should not be null"));
    }

    public static ResponseHandler seq(final String... contents) {
        return seq(FluentIterable.from(copyOf(contents)).transform(textToResource()).toList());
    }

    public static ResponseHandler seq(final Resource... contents) {
        return seq(FluentIterable.from(copyOf(contents)).transform(resourceToResourceHandler()).toList());
    }

    public static ResponseHandler seq(final ResponseHandler... handlers) {
        return seq(copyOf(handlers));
    }

    private static ResponseHandler seq(ImmutableList<ResponseHandler> list) {
        return new SequenceContentHandler(list);
    }

    public static ContentResource file(final String filename) {
        return fileResource(new File(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource pathResource(final String filename) {
        return classpathFileResource(checkNotNullOrEmpty(filename, "Filename should not be null"));
    }

    public static Resource version(final Resource resource) {
        return versionResource(checkNotNull(resource, "Version should not be null"));
    }

    public static Resource version(final String version) {
        return version(HttpProtocolVersion.versionOf(checkNotNullOrEmpty(version, "Version should not be null")));
    }

    public static Resource version(final HttpProtocolVersion version) {
        return version(text((checkNotNull(version, "Version should not be null")).text()));
    }

    public static ResponseHandler status(final int code) {
        checkArgument(code > 0, "Status code must be greater than zero");
        return new StatusCodeResponseHandler(code);
    }

    public static ResponseHandler proxy(final String url) {
        return proxy(checkNotNullOrEmpty(url, "URL should not be null"), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final String url, final Failover failover) {
        return new ProxyResponseHandler(URLs.toUrl(checkNotNullOrEmpty(url, "URL should not be null")),
                checkNotNull(failover, "Failover should not be null"));
    }

    public static ResponseHandler proxy(final ProxyConfig proxyConfig) {
        return proxy(checkNotNull(proxyConfig), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final ProxyConfig proxyConfig, final Failover failover) {
        return new ProxyBatchResponseHandler(checkNotNull(proxyConfig), checkNotNull(failover));
    }

    public static ProxyConfig.Builder from(final String localBase) {
        return ProxyConfig.builder(checkNotNullOrEmpty(localBase, "Local base should not be null"));
    }

    public static Resource template(final String template) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")));
    }

    public static Resource template(final String template, final String name, final String value) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                ImmutableMap.of(checkNotNullOrEmpty(name, "Template variable name should not be null"),
                        checkNotNullOrEmpty(value, "Template variable value should not be null")));
    }

    public static Resource template(final String template, final String name1, final String value1, final String name2, final String value2) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                ImmutableMap.of(checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                        checkNotNullOrEmpty(value1, "Template variable value should not be null"),
                        checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                        checkNotNullOrEmpty(value2, "Template variable value should not be null")));
    }

    public static Resource template(final String template, final ImmutableMap<String, String> variables) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNull(variables, "Template variable should not be null"));
    }

    public static Resource template(final ContentResource template, final ImmutableMap<String, String> variables) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                checkNotNull(variables, "Template variable should not be null"));
    }

    public static Resource template(final ContentResource resource) {
        return template(checkNotNull(resource, "Template should not be null"), ImmutableMap.<String, String>of());
    }

    public static Resource template(final ContentResource template, final String name, final String value) {
        return template(checkNotNull(template, "Template should not be null"),
                ImmutableMap.of(checkNotNullOrEmpty(name, "Template variable name should not be null"),
                        checkNotNullOrEmpty(value, "Template variable value should not be null")));
    }

    public static Resource template(final ContentResource template, final String name1, final String value1, final String name2, final String value2) {
        return template(checkNotNull(template, "Template should not be null"),
                ImmutableMap.of(checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                        checkNotNullOrEmpty(value1, "Template variable value should not be null"),
                        checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                        checkNotNullOrEmpty(value2, "Template variable value should not be null")));
    }

    public static Resource custom(final Function<Request, String> function) {
        return customResource(checkNotNull(function, "Function should not be null"));
    }

    public static Failover failover(final String file) {
        return new Failover(failoverExecutor(checkNotNullOrEmpty(file, "Filename should not be null")), FailoverStrategy.FAILOVER);
    }

    private static DefaultFailoverExecutor failoverExecutor(String file) {
        return new DefaultFailoverExecutor(new File(checkNotNullOrEmpty(file, "Filename should not be null")));
    }

    public static Failover playback(final String file) {
        return new Failover(failoverExecutor(checkNotNullOrEmpty(file, "Filename should not be null")), FailoverStrategy.PLAYBACK);
    }

    public static MocoEventTrigger complete(final MocoEventAction action) {
        return new MocoEventTrigger(MocoEvent.COMPLETE, checkNotNull(action, "Action should not be null"));
    }

    public static MocoEventAction async(final MocoEventAction action) {
        return async(checkNotNull(action, "Action should not be null"), latency(LatencyProcedure.DEFAULT_LATENCY));
    }

    public static MocoEventAction async(final MocoEventAction action, final LatencyProcedure procedure) {
        return new MocoAsyncAction(checkNotNull(action, "Action should not be null"), checkNotNull(procedure, "Procedure should not be null"));
    }

    public static MocoEventAction get(final String url) {
        return new MocoRequestAction(checkNotNullOrEmpty(url, "URL should not be null"), "GET", Optional.<ContentResource>absent());
    }

    public static MocoEventAction post(final String url, final ContentResource content) {
        return new MocoRequestAction(checkNotNullOrEmpty(url, "URL should not be null"), "POST", of(checkNotNull(content, "Content should not be null")));
    }

    public static MocoEventAction post(final String url, final String content) {
        return post(checkNotNullOrEmpty(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")));
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

    private Moco() {
    }
}
