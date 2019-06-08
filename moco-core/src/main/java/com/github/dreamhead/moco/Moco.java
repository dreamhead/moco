package com.github.dreamhead.moco;

import com.github.dreamhead.moco.action.MocoAsyncAction;
import com.github.dreamhead.moco.action.MocoGetRequestAction;
import com.github.dreamhead.moco.action.MocoPostRequestAction;
import com.github.dreamhead.moco.config.MocoContextConfig;
import com.github.dreamhead.moco.config.MocoFileRootConfig;
import com.github.dreamhead.moco.config.MocoRequestConfig;
import com.github.dreamhead.moco.config.MocoResponseConfig;
import com.github.dreamhead.moco.extractor.CookieRequestExtractor;
import com.github.dreamhead.moco.extractor.FormRequestExtractor;
import com.github.dreamhead.moco.extractor.HeaderRequestExtractor;
import com.github.dreamhead.moco.extractor.JsonPathRequestExtractor;
import com.github.dreamhead.moco.extractor.ParamRequestExtractor;
import com.github.dreamhead.moco.extractor.PlainExtractor;
import com.github.dreamhead.moco.extractor.XPathRequestExtractor;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.HeaderResponseHandler;
import com.github.dreamhead.moco.handler.HttpHeaderResponseHandler;
import com.github.dreamhead.moco.handler.ProcedureResponseHandler;
import com.github.dreamhead.moco.handler.ProxyBatchResponseHandler;
import com.github.dreamhead.moco.handler.ProxyResponseHandler;
import com.github.dreamhead.moco.handler.StatusCodeResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.failover.FailoverStrategy;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ActualSocketServer;
import com.github.dreamhead.moco.internal.ApiUtils;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.ExistMatcher;
import com.github.dreamhead.moco.matcher.NotRequestMatcher;
import com.github.dreamhead.moco.matcher.OrRequestMatcher;
import com.github.dreamhead.moco.matcher.XmlRequestMatcher;
import com.github.dreamhead.moco.monitor.StdLogWriter;
import com.github.dreamhead.moco.procedure.LatencyProcedure;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.ExtractorVariable;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.extractor.Extractors.extractor;
import static com.github.dreamhead.moco.handler.CycleHandler.newCycle;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.github.dreamhead.moco.handler.SequenceHandler.newSeq;
import static com.github.dreamhead.moco.internal.ApiUtils.resourceToResourceHandler;
import static com.github.dreamhead.moco.internal.ApiUtils.textToResource;
import static com.github.dreamhead.moco.internal.ApiUtils.toHeaders;
import static com.github.dreamhead.moco.resource.ResourceFactory.cookieResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.jsonResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.methodResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.templateResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.textResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.uriResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.versionResource;
import static com.github.dreamhead.moco.resource.reader.TemplateResourceReader.checkValidVariableName;
import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.github.dreamhead.moco.util.URLs.toUrlFunction;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.HttpHeaders.SET_COOKIE;
import static java.lang.String.format;

public final class Moco {
    public static HttpServer httpServer(final int port, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createQuietServer(port, configs);
    }

    public static HttpServer httpServer(final int port, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(port,
                checkNotNull(monitor, "Monitor should not be null"),
                checkNotNull(configs, "Configuration should not be null"));
    }

    public static HttpServer httpServer(final int port, final MocoMonitor monitor, final MocoMonitor monitor2,
                                        final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(port,
                ApiUtils.mergeMonitor(checkNotNull(monitor, "Monitor should not be null"),
                        checkNotNull(monitor2, "Monitor should not be null"),
                        checkNotNull(monitors, "Monitors should not be null")));
    }

    public static HttpServer httpServer(final MocoConfig... configs) {
        return ActualHttpServer.createQuietServer(0,
                checkNotNull(configs, "Configuration should not be null"));
    }

    public static HttpServer httpServer(final MocoMonitor monitor, final MocoConfig... configs) {
        return ActualHttpServer.createHttpServerWithMonitor(0, checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpsServer httpsServer(final int port, final HttpsCertificate certificate, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsQuietServer(port, checkNotNull(certificate, "Certificate should not be null"), configs);
    }

    public static HttpsServer httpsServer(final int port, final HttpsCertificate certificate, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsServerWithMonitor(port,
                checkNotNull(certificate, "Certificate should not be null"),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpsServer httpsServer(final HttpsCertificate certificate, final MocoConfig... configs) {
        return ActualHttpServer.createHttpsQuietServer(0, checkNotNull(certificate, "Certificate should not be null"), configs);
    }

    public static HttpsServer httpsServer(final HttpsCertificate certificate, final MocoMonitor monitor, final MocoConfig... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(0,
                checkNotNull(certificate, "Certificate should not be null"),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpServer httpsServer(final int port, final HttpsCertificate certificate, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsServerWithMonitor(port, checkNotNull(certificate, "Certificate should not be null"),
                ApiUtils.mergeMonitor(checkNotNull(monitor, "Monitor should not be null"),
                        checkNotNull(monitor2, "Monitor should not be null"), monitors));
    }

    public static SocketServer socketServer() {
        return ActualSocketServer.createQuietServer(0);
    }

    public static SocketServer socketServer(final int port) {
        return ActualSocketServer.createQuietServer(port);
    }

    public static SocketServer socketServer(final int port, final MocoMonitor monitor) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualSocketServer.createServerWithMonitor(port,
                checkNotNull(monitor, "Monitor should not be null"));
    }

    public static SocketServer socketServer(final int port, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualSocketServer.createServerWithMonitor(port,
                ApiUtils.mergeMonitor(checkNotNull(monitor, "Monitor should not be null"),
                        checkNotNull(monitor2, "Monitor should not be null"), monitors));
    }


    public static MocoConfig context(final String context) {
        return new MocoContextConfig(checkNotNullOrEmpty(context, "Context should not be null"));
    }

    public static MocoConfig request(final RequestMatcher matcher) {
        return new MocoRequestConfig(checkNotNull(matcher, "Request matcher should not be null"));
    }

    public static MocoConfig response(final ResponseHandler handler) {
        return new MocoResponseConfig(checkNotNull(handler, "Response handler should not be null"));
    }

    public static MocoConfig response(final HttpHeader header) {
        return response(with(checkNotNull(header, "Response handler should not be null")));
    }

    public static MocoConfig fileRoot(final String fileRoot) {
        return new MocoFileRootConfig(checkNotNullOrEmpty(fileRoot, "File root should not be null"));
    }

    public static MocoMonitor log() {
        return ApiUtils.log(new StdLogWriter());
    }

    public static MocoMonitor log(final String filename) {
        return ApiUtils.log(ApiUtils.fileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty"), null));
    }

    public static MocoMonitor log(final String filename, final Charset charset) {
        return ApiUtils.log(ApiUtils.fileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty"), checkNotNull(charset, "Charset should not be null")));
    }

    public static RequestMatcher by(final String content) {
        return by(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static RequestMatcher by(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return ApiUtils.by(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final String expected) {
        return eq(checkNotNull(extractor, "Extractor should not be null"), text(checkNotNull(expected, "Expected content should not be null")));
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final Resource expected) {
        return new EqRequestMatcher<>(checkNotNull(extractor, "Extractor should not be null"), checkNotNull(expected, "Expected content should not be null"));
    }

    public static RequestMatcher match(final Resource resource) {
        return ApiUtils.match(extractor(resource.id()), checkNotNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.match(checkNotNull(extractor, "Extractor should not be null"), text(checkNotNullOrEmpty(expected, "Expected content should not be null")));
    }

    public static <T> RequestMatcher exist(final RequestExtractor<T> extractor) {
        return new ExistMatcher<>(checkNotNull(extractor, "Extractor should not be null"));
    }

    public static RequestMatcher startsWith(final Resource resource) {
        return ApiUtils.startsWith(extractor(resource.id()), checkNotNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher startsWith(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.startsWith(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    public static RequestMatcher endsWith(final Resource resource) {
        return ApiUtils.endsWith(extractor(resource.id()), checkNotNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher endsWith(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.endsWith(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    public static RequestMatcher contain(final Resource resource) {
        return ApiUtils.contain(extractor(resource.id()), checkNotNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher contain(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.contain(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    public static RequestMatcher and(final RequestMatcher matcher, final RequestMatcher... matchers) {
        return new AndRequestMatcher(asIterable(
                checkNotNull(matcher, "Matcher should not be null"),
                checkNotNull(matchers, "Matcher should not be null")));
    }

    public static ResponseHandler and(final ResponseHandler handler, final ResponseHandler... handlers) {
        return AndResponseHandler.and(
                checkNotNull(handler, "Handlers should not be null"),
                checkNotNull(handlers, "Handlers should not be null"));
    }

    public static RequestMatcher or(final RequestMatcher matcher, final RequestMatcher... matchers) {
        return new OrRequestMatcher(asIterable(
                checkNotNull(matcher, "Matcher should not be null"),
                checkNotNull(matchers, "Matcher should not be null")));
    }

    public static RequestMatcher not(final RequestMatcher matcher) {
        return new NotRequestMatcher(checkNotNull(matcher, "Expected matcher should not be null"));
    }

    public static ContentResource text(final String text) {
        return textResource(checkNotNull(text, "Text should not be null"));
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

    public static ResponseHandler with(final HttpHeader header) {
        return new HttpHeaderResponseHandler(checkNotNull(header, "HTTP header should not be null"));
    }

    public static Resource uri(final String uri) {
        return uriResource(checkNotNullOrEmpty(uri, "URI should not be null"));
    }

    public static Resource method(final String httpMethod) {
        return methodResource(checkNotNullOrEmpty(httpMethod, "HTTP method should not be null"));
    }

    public static Resource method(final HttpMethod httpMethod) {
        return methodResource(checkNotNull(httpMethod, "HTTP method should not be null").toString());
    }

    public static RequestExtractor<String[]> header(final String header) {
        return new HeaderRequestExtractor(checkNotNullOrEmpty(header, "Header name should not be null"));
    }

    public static ResponseHandler header(final String name, final String value) {
        return header(checkNotNullOrEmpty(name, "Header name should not be null"), text(checkNotNullOrEmpty(value, "Header value should not be null")));
    }

    public static ResponseHandler header(final String name, final Resource value) {
        return new HeaderResponseHandler(checkNotNullOrEmpty(name, "Header name should not be null"),
                checkNotNull(value, "Header value should not be null"));
    }

    public static HttpHeader asHeader(final String name, final String value) {
        return asHeader(checkNotNullOrEmpty(name, "Header name should not be null"),
                text(checkNotNull(value, "Header value should not be null")));
    }

    public static HttpHeader asHeader(final String name, final Resource value) {
        return new HttpHeader(checkNotNullOrEmpty(name, "Header name should not be null"),
                checkNotNull(value, "Header value should not be null"));
    }

    public static RequestExtractor<String> cookie(final String key) {
        return new CookieRequestExtractor(checkNotNullOrEmpty(key, "Cookie key should not be null"));
    }

    public static ResponseHandler cookie(final String key, final String value, final CookieAttribute... attributes) {
        return cookie(checkNotNullOrEmpty(key, "Cookie key should not be null"),
                text(checkNotNullOrEmpty(value, "Cookie value should not be null")),
                checkNotNull(attributes, "Cookie options should not be null"));
    }

    public static ResponseHandler cookie(final String key, final Resource resource, final CookieAttribute... attributes) {
        return with(asHeader(SET_COOKIE, cookieResource(
                checkNotNullOrEmpty(key, "Cookie key should not be null"),
                checkNotNull(resource, "Cookie value should not be null"),
                checkNotNull(attributes, "Cookie options should not be null"))));
    }

    public static RequestExtractor<String> form(final String key) {
        return new FormRequestExtractor(checkNotNullOrEmpty(key, "Form key should not be null"));
    }

    public static LatencyProcedure latency(final long duration, final TimeUnit unit) {
        checkArgument(duration > 0, "Latency must be greater than zero");
        return new LatencyProcedure(duration, checkNotNull(unit, "Time unit should not be null"));
    }

    public static RequestExtractor<String[]> query(final String param) {
        return new ParamRequestExtractor(checkNotNullOrEmpty(param, "Query parameter should not be null"));
    }

    public static XPathRequestExtractor xpath(final String xpath) {
        return new XPathRequestExtractor(checkNotNullOrEmpty(xpath, "XPath should not be null"));
    }

    public static RequestMatcher xml(final String resource) {
        return xml(text(checkNotNullOrEmpty(resource, "Resource should not be null")));
    }

    public static RequestMatcher xml(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
        return new XmlRequestMatcher(resource);
    }

    public static ContentResource json(final String jsonText) {
        return json(text(checkNotNullOrEmpty(jsonText, "Json should not be null")));
    }

    public static ContentResource json(final Resource resource) {
        return jsonResource(checkNotNull(resource, "Json should not be null"));
    }

    public static ContentResource json(final Object pojo) {
        return jsonResource(checkNotNull(pojo, "Json object should not be null"));
    }

    public static JsonPathRequestExtractor jsonPath(final String jsonPath) {
        return new JsonPathRequestExtractor(checkNotNullOrEmpty(jsonPath, "JsonPath should not be null"));
    }

    public static ResponseHandler seq(final String content, final String... contents) {
        checkNotNull(content, "Sequence content should not be null");
        checkArgument(contents.length > 0, "Sequence content should not be null");
        return newSeq(FluentIterable.from(asIterable(content, contents)).transform(textToResource()));
    }

    public static ResponseHandler seq(final Resource content, final Resource... contents) {
        checkNotNull(content, "Sequence content should not be null");
        checkArgument(contents.length > 0, "Sequence contents should not be null");
        return newSeq(FluentIterable.from(asIterable(content, contents)).transform(resourceToResourceHandler()));
    }

    public static ResponseHandler seq(final ResponseHandler handler, final ResponseHandler... handlers) {
        checkNotNull(handler, "Sequence handler should not be null");
        checkArgument(handlers.length > 0, "Sequence handlers should not be null");
        return newSeq(asIterable(handler, handlers));
    }

    public static ResponseHandler cycle(final String content, final String... contents) {
        checkNotNull(content, "Cycle content should not be null");
        checkArgument(contents.length > 0, "Cycle content should not be null");
        return newCycle(FluentIterable.from(asIterable(content, contents)).transform(textToResource()));
    }

    public static ResponseHandler cycle(final Resource content, final Resource... contents) {
        checkNotNull(content, "Cycle content should not be null");
        checkArgument(contents.length > 0, "Cycle contents should not be null");
        return newCycle(FluentIterable.from(asIterable(content, contents)).transform(resourceToResourceHandler()));
    }

    public static ResponseHandler cycle(final ResponseHandler handler, final ResponseHandler... handlers) {
        checkNotNull(handler, "Cycle handler should not be null");
        checkArgument(handlers.length > 0, "Cycle handlers should not be null");
        return newCycle(asIterable(handler, handlers));
    }

    public static ContentResource file(final String filename) {
        return file(text(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource file(final Resource filename) {
        return ApiUtils.file(checkNotNull(filename, "Filename should not be null"), null);
    }

    public static ContentResource file(final String filename, final Charset charset) {
        return ApiUtils.file(text(checkNotNullOrEmpty(filename, "Filename should not be null")), checkNotNull(charset, "Charset should not be null"));
    }

    public static ContentResource file(final Resource filename, final Charset charset) {
        return ApiUtils.file(checkNotNull(filename, "Filename should not be null"), checkNotNull(charset, "Charset should not be null"));
    }

    public static ContentResource pathResource(final String filename) {
        return pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource pathResource(final Resource filename) {
        return ApiUtils.pathResource(checkNotNull(filename, "Filename should not be null"), null);
    }

    public static ContentResource pathResource(final String filename, final Charset charset) {
        return ApiUtils.pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")), checkNotNull(charset, "Charset should not be null"));
    }

    public static ContentResource pathResource(final Resource filename, final Charset charset) {
        return ApiUtils.pathResource(checkNotNull(filename, "Filename should not be null"), checkNotNull(charset, "Charset should not be null"));
    }

    public static Resource version(final String version) {
        return version(HttpProtocolVersion.versionOf(checkNotNullOrEmpty(version, "Version should not be null")));
    }

    public static Resource version(final Resource resource) {
        return versionResource(checkNotNull(resource, "Version should not be null"));
    }

    public static Resource version(final HttpProtocolVersion version) {
        return versionResource(checkNotNull(version, "Version should not be null"));
    }

    public static ResponseHandler status(final int code) {
        checkArgument(code > 0, "Status code must be greater than zero");
        return new StatusCodeResponseHandler(code);
    }

    public static ResponseHandler proxy(final String url) {
        return proxy(checkNotNullOrEmpty(url, "URL should not be null"), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final ContentResource url) {
        return proxy(checkNotNull(url, "URL should not be null"), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final String url, final Failover failover) {
        return proxy(text(checkNotNullOrEmpty(url, "URL should not be null")),
                checkNotNull(failover, "Failover should not be null"));
    }

    public static ResponseHandler proxy(final ContentResource url, final Failover failover) {
        return new ProxyResponseHandler(toUrlFunction(checkNotNull(url, "URL should not be null")),
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

    public static ContentResource template(final String template) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")));
    }

    public static ContentResource template(final String template, final String name, final String value) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                checkNotNullOrEmpty(value, "Template variable value should not be null"));
    }

    public static ContentResource template(final String template, final String name1, final String value1, final String name2, final String value2) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                checkNotNullOrEmpty(value1, "Template variable value should not be null"),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                checkNotNullOrEmpty(value2, "Template variable value should not be null"));
    }

    public static ContentResource template(final ContentResource resource) {
        return template(checkNotNull(resource, "Template should not be null"), ImmutableMap.<String, RequestExtractor<?>>of());
    }

    public static ContentResource template(final ContentResource template, final String name, final String value) {
        return template(checkNotNull(template, "Template should not be null"),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value, "Template variable value should not be null")));
    }

    public static ContentResource template(final ContentResource template, final String name1, final String value1, final String name2, final String value2) {
        return template(checkNotNull(template, "Template should not be null"),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value1, "Template variable value should not be null")),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value2, "Template variable value should not be null")));
    }

    public static <T> ContentResource template(final String template, final String name, final RequestExtractor<T> extractor) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                checkNotNull(extractor, "Template variable extractor should not be null"));
    }

    public static <ExtractorType1, ExtractorType2> ContentResource template(final String template, final String name1, final RequestExtractor<ExtractorType1> extractor1,
                                                                            final String name2, final RequestExtractor<ExtractorType2> extractor2) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                checkNotNull(extractor1, "Template variable extractor should not be null"),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                checkNotNull(extractor2, "Template variable extractor should not be null"));
    }

    public static <T> ContentResource template(final ContentResource template, final String name, final RequestExtractor<T> extractor) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                ImmutableMap.of(checkValidVariableName(name),
                        new ExtractorVariable<>(checkNotNull(extractor, "Template variable extractor should not be null")))
        );
    }

    public static <ExtractorType1, ExtractorType2> ContentResource template(final ContentResource template, final String name1, final RequestExtractor<ExtractorType1> extractor1,
                                                                            final String name2, final RequestExtractor<ExtractorType2> extractor2) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                ImmutableMap.of(checkValidVariableName(name1),
                        new ExtractorVariable<>(checkNotNull(extractor1, "Template variable extractor should not be null")),
                        checkValidVariableName(name2),
                        new ExtractorVariable<>(checkNotNull(extractor2, "Template variable extractor should not be null")))
        );
    }

    public static ContentResource template(final String template,
                                           final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
        return template(text(checkNotNull(template, "Template should not be null")),
                checkNotNull(variables, "Template variable should not be null"));
    }

    public static ContentResource template(final ContentResource template,
                                           final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                ApiUtils.toVariables(checkNotNull(variables, "Template variable should not be null")));
    }

    public static RequestExtractor<Object> var(final Object text) {
        return new PlainExtractor<>(checkNotNull(text, "Template variable should not be null or empty"));
    }

    public static Failover failover(final String file, final int... statuses) {
        return new Failover(ApiUtils.failoverExecutor(
                checkNotNullOrEmpty(file, "Failover filename should not be null")), FailoverStrategy.FAILOVER,
                checkNotNull(statuses, "Proxy status should not be null"));
    }

    public static Failover playback(final String file, final int... statuses) {
        return new Failover(ApiUtils.failoverExecutor(
                checkNotNullOrEmpty(file, "Playback filename should not be null")), FailoverStrategy.PLAYBACK,
                checkNotNull(statuses, "Proxy status should not be null"));
    }

    public static MocoEventTrigger complete(final MocoEventAction action) {
        return new MocoEventTrigger(MocoEvent.COMPLETE, checkNotNull(action, "Action should not be null"));
    }

    private static final int DEFAULT_LATENCY = 1000;

    public static MocoEventAction async(final MocoEventAction action) {
        return async(checkNotNull(action, "Action should not be null"),
                latency(DEFAULT_LATENCY, TimeUnit.MILLISECONDS));
    }

    public static MocoEventAction async(final MocoEventAction action, final LatencyProcedure procedure) {
        return new MocoAsyncAction(checkNotNull(action, "Action should not be null"),
                checkNotNull(procedure, "Procedure should not be null"));
    }

    public static MocoEventAction get(final String url) {
        return get(text(checkNotNullOrEmpty(url, "URL should not be null")));
    }

    public static MocoEventAction get(final String url, final HttpHeader header, final HttpHeader... headers) {
        return get(text(checkNotNullOrEmpty(url, "URL should not be null")),
                checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction get(final Resource url) {
        return new MocoGetRequestAction(checkNotNull(url, "URL should not be null"), ImmutableMap.<String, Resource>of());
    }

    public static MocoEventAction get(final Resource url, final HttpHeader header, final HttpHeader... headers) {
        Iterable<HttpHeader> httpHeaders = asIterable(checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Headers should not be null"));
        return new MocoGetRequestAction(checkNotNull(url, "URL should not be null"), toHeaders(httpHeaders));
    }

    public static MocoEventAction post(final Resource url, final ContentResource content) {
        return new MocoPostRequestAction(checkNotNull(url, "URL should not be null"), checkNotNull(content, "Content should not be null"), ImmutableMap.<String, Resource>of());
    }

    public static MocoEventAction post(final Resource url, final ContentResource content, final HttpHeader header, final HttpHeader... headers) {
        Iterable<HttpHeader> httpHeaders = asIterable(checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Header should not be null"));
        return new MocoPostRequestAction(checkNotNull(url, "URL should not be null"), checkNotNull(content, "Content should not be null"),
                toHeaders(httpHeaders));
    }

    public static MocoEventAction post(final String url, final ContentResource content) {
        return post(text(checkNotNullOrEmpty(url, "URL should not be null")), checkNotNull(content, "Content should not be null"));
    }

    public static MocoEventAction post(final String url, final ContentResource content, final HttpHeader header, final HttpHeader... headers) {
        return post(text(checkNotNullOrEmpty(url, "URL should not be null")), checkNotNull(content, "Content should not be null"),
                checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final String url, final String content) {
        return post(checkNotNullOrEmpty(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static MocoEventAction post(final String url, final String content, final HttpHeader header, final HttpHeader... headers) {
        return post(checkNotNullOrEmpty(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")),
                checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final Resource url, final String content) {
        return post(checkNotNull(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static MocoEventAction post(final Resource url, final String content, final HttpHeader header, final HttpHeader... headers) {
        return post(checkNotNull(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")),
                checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final Resource url, final Object object) {
        return post(checkNotNull(url, "URL should not be null"),
                Jsons.toJson(checkNotNull(object, "Content should not be null")));
    }

    public static MocoEventAction post(final Resource url, final Object object, final HttpHeader header, final HttpHeader... headers) {
        return post(checkNotNull(url, "URL should not be null"),
                Jsons.toJson(checkNotNull(object, "Content should not be null")),
                checkNotNull(header, "Header should not be null"),
                checkNotNull(headers, "Headers should not be null"));
    }

    public static ResponseHandler attachment(final String filename, final Resource resource) {
        return AndResponseHandler.and(
                with(asHeader(HttpHeaders.CONTENT_DISPOSITION, format("attachment; filename=%s", checkNotNullOrEmpty(filename, "Filename should not be null or empty")))),
                with(checkNotNull(resource, "Resource should not be null")));
    }

    private Moco() {
    }
}
