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
import com.github.dreamhead.moco.extractor.FunctionExtractor;
import com.github.dreamhead.moco.extractor.HeaderRequestExtractor;
import com.github.dreamhead.moco.extractor.JsonPathRequestExtractor;
import com.github.dreamhead.moco.extractor.ParamRequestExtractor;
import com.github.dreamhead.moco.extractor.XPathRequestExtractor;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.ProxyBatchResponseHandler;
import com.github.dreamhead.moco.handler.ProxyResponseHandler;
import com.github.dreamhead.moco.handler.StatusCodeResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.failover.FailoverStrategy;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ApiUtils;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.ConditionalRequestMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.ExistMatcher;
import com.github.dreamhead.moco.matcher.NotRequestMatcher;
import com.github.dreamhead.moco.matcher.OrRequestMatcher;
import com.github.dreamhead.moco.monitor.StdLogWriter;
import com.github.dreamhead.moco.procedure.LatencyProcedure;
import com.github.dreamhead.moco.recorder.MocoGroup;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.ExtractorVariable;

import com.github.dreamhead.moco.util.HttpHeaders;
import com.github.dreamhead.moco.util.Jsons;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dreamhead.moco.extractor.Extractors.extractor;
import static com.github.dreamhead.moco.handler.CycleHandler.newCycle;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.github.dreamhead.moco.handler.SequenceHandler.newSeq;
import static com.github.dreamhead.moco.internal.ApiUtils.textToResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.binaryResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.cookieResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.jsonResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.methodResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.templateResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.textResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.uriResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.versionResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.xmlResource;
import static com.github.dreamhead.moco.resource.reader.TemplateResourceReader.checkValidVariableName;
import static com.github.dreamhead.moco.util.HttpHeaders.SET_COOKIE;
import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.github.dreamhead.moco.util.Preconditions.checkArgument;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.github.dreamhead.moco.util.URLs.toUrlFunction;
import static java.util.Objects.requireNonNull;

public final class Moco {
    public static HttpServer httpServer(final int port, final MocoConfig<?>... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createQuietServer(port, requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpServer httpServer(final int port, final MocoMonitor monitor, final MocoConfig<?>... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(port,
                requireNonNull(monitor, "Monitor should not be null"),
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpServer httpServer(final int port, final MocoMonitor monitor, final MocoMonitor monitor2,
                                        final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(port,
                ApiUtils.mergeMonitor(requireNonNull(monitor, "Monitor should not be null"),
                        requireNonNull(monitor2, "Monitor should not be null"),
                        requireNonNull(monitors, "Monitors should not be null")));
    }

    public static HttpServer httpServer(final MocoConfig<?>... configs) {
        return ActualHttpServer.createQuietServer(0,
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpServer httpServer(final MocoMonitor monitor, final MocoConfig<?>... configs) {
        return ActualHttpServer.createHttpServerWithMonitor(0, requireNonNull(monitor, "Monitor should not be null"),
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpsServer httpsServer(final int port, final HttpsCertificate certificate, final MocoConfig<?>... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsQuietServer(port, requireNonNull(certificate, "Certificate should not be null"),
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpsServer httpsServer(final int port, final HttpsCertificate certificate, final MocoMonitor monitor, final MocoConfig<?>... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsServerWithMonitor(port,
                requireNonNull(certificate, "Certificate should not be null"),
                requireNonNull(monitor, "Monitor should not be null"),
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpsServer httpsServer(final HttpsCertificate certificate, final MocoConfig<?>... configs) {
        return ActualHttpServer.createHttpsQuietServer(0, requireNonNull(certificate, "Certificate should not be null"),
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpsServer httpsServer(final HttpsCertificate certificate, final MocoMonitor monitor, final MocoConfig<?>... configs) {
        return ActualHttpServer.createHttpsServerWithMonitor(0,
                requireNonNull(certificate, "Certificate should not be null"),
                requireNonNull(monitor, "Monitor should not be null"),
                requireNonNull(configs, "Configuration should not be null"));
    }

    public static HttpServer httpsServer(final int port, final HttpsCertificate certificate, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpsServerWithMonitor(port, requireNonNull(certificate, "Certificate should not be null"),
                ApiUtils.mergeMonitor(requireNonNull(monitor, "Monitor should not be null"),
                        requireNonNull(monitor2, "Monitor should not be null"),
                        requireNonNull(monitors, "Monitors should not be null")));
    }

    public static MocoConfig<?> context(final String context) {
        return new MocoContextConfig(checkNotNullOrEmpty(context, "Context should not be null"));
    }

    public static MocoConfig<?> request(final RequestMatcher matcher) {
        return new MocoRequestConfig(requireNonNull(matcher, "Request matcher should not be null"));
    }

    public static MocoConfig<?> response(final ResponseHandler handler) {
        return new MocoResponseConfig(requireNonNull(handler, "Response handler should not be null"));
    }

    public static MocoConfig<?> response(final HttpHeader header) {
        return response(with(requireNonNull(header, "Response handler should not be null")));
    }

    public static MocoConfig<?> fileRoot(final String fileRoot) {
        return new MocoFileRootConfig(checkNotNullOrEmpty(fileRoot, "File root should not be null"));
    }

    public static MocoMonitor log() {
        return ApiUtils.log(new StdLogWriter());
    }

    public static MocoMonitor log(final String filename) {
        return ApiUtils.log(ApiUtils.fileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty"), null));
    }

    public static MocoMonitor log(final String filename, final Charset charset) {
        return ApiUtils.log(ApiUtils.fileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty"), requireNonNull(charset, "Charset should not be null")));
    }

    public static RequestMatcher by(final String content) {
        return by(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static RequestMatcher by(final Resource resource) {
        requireNonNull(resource, "Resource should not be null");
        return ApiUtils.by(extractor(resource.id()), resource);
    }

    public static RequestMatcher struct(final Resource resource) {
        requireNonNull(resource, "Resource should not be null");
        return ApiUtils.struct(extractor(resource.id()), resource);
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final String expected) {
        return eq(requireNonNull(extractor, "Extractor should not be null"), text(requireNonNull(expected, "Expected content should not be null")));
    }

    public static <T> RequestMatcher eq(final RequestExtractor<T> extractor, final Resource expected) {
        return new EqRequestMatcher<>(requireNonNull(extractor, "Extractor should not be null"), requireNonNull(expected, "Expected content should not be null"));
    }

    public static RequestMatcher match(final Resource resource) {
        return ApiUtils.match(extractor(resource.id()), requireNonNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.match(requireNonNull(extractor, "Extractor should not be null"), text(checkNotNullOrEmpty(expected, "Expected content should not be null")));
    }

    public static RequestMatcher path(final Resource resource) {
        return ApiUtils.path(extractor(resource.id()), requireNonNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher exist(final RequestExtractor<T> extractor) {
        return new ExistMatcher<>(requireNonNull(extractor, "Extractor should not be null"));
    }

    public static RequestMatcher startsWith(final Resource resource) {
        return ApiUtils.startsWith(extractor(resource.id()), requireNonNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher startsWith(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.startsWith(requireNonNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    public static RequestMatcher endsWith(final Resource resource) {
        return ApiUtils.endsWith(extractor(resource.id()), requireNonNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher endsWith(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.endsWith(requireNonNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    public static RequestMatcher contain(final Resource resource) {
        return ApiUtils.contain(extractor(resource.id()), requireNonNull(resource, "Resource should not be null"));
    }

    public static <T> RequestMatcher contain(final RequestExtractor<T> extractor, final String expected) {
        return ApiUtils.contain(requireNonNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    public static RequestMatcher and(final RequestMatcher matcher, final RequestMatcher... matchers) {
        return new AndRequestMatcher(asIterable(
                requireNonNull(matcher, "Matcher should not be null"),
                requireNonNull(matchers, "Matcher should not be null")));
    }

    public static ResponseHandler and(final ResponseElement element, final ResponseElement... elements) {
        return AndResponseHandler.and(
                requireNonNull(element, "Response should not be null"),
                requireNonNull(elements, "Responses should not be null"));
    }

    public static RequestMatcher or(final RequestMatcher matcher, final RequestMatcher... matchers) {
        return new OrRequestMatcher(asIterable(
                requireNonNull(matcher, "Matcher should not be null"),
                requireNonNull(matchers, "Matcher should not be null")));
    }

    public static RequestMatcher not(final RequestMatcher matcher) {
        return new NotRequestMatcher(requireNonNull(matcher, "Expected matcher should not be null"));
    }

    public static ContentResource text(final String text) {
        requireNonNull(text, "Text should not be null");
        return text((request) -> text);
    }

    public static ContentResource text(final Function<Request, String> function) {
        return textResource(requireNonNull(function, "Text function should not be null"));
    }

    public static ContentResource binary(final byte[] binary) {
        requireNonNull(binary, "Binary should not be null");
        return binary((request) -> binary);
    }

    public static ContentResource binary(final ByteBuffer buffer) {
        requireNonNull(buffer, "Binary should not be null");
        return binary(buffer.array());
    }

    public static ContentResource binary(final InputStream stream) {
        requireNonNull(stream, "Binary stream should not be null");
        return binary((request) -> stream);
    }

    public static ContentResource binary(final Function<Request, Object> function) {
        return binaryResource(requireNonNull(function, "Binary function should not be null"));
    }

    public static ResponseHandler with(final ResponseElement element) {
        return responseHandler(requireNonNull(element, "Response element should not be null"));
    }

    public static ResponseHandler with(final String text) {
        return with(text(checkNotNullOrEmpty(text, "Text should not be null")));
    }

    public static Resource uri(final String uri) {
        return uriResource(checkNotNullOrEmpty(uri, "URI should not be null"));
    }

    public static Resource method(final String httpMethod) {
        return methodResource(checkNotNullOrEmpty(httpMethod, "HTTP method should not be null"));
    }

    public static Resource method(final HttpMethod httpMethod) {
        return methodResource(requireNonNull(httpMethod, "HTTP method should not be null").toString());
    }

    public static RequestExtractor<String[]> header(final String header) {
        return new HeaderRequestExtractor(checkNotNullOrEmpty(header, "Header name should not be null"));
    }

    public static HttpHeader header(final String name, final String value) {
        return new HttpHeader(checkNotNullOrEmpty(checkNotNullOrEmpty(name, "Header name should not be null"), "Header name should not be null"),
                requireNonNull((Resource) text(checkNotNullOrEmpty(value, "Header value should not be null")), "Header value should not be null"));
    }

    public static HttpHeader header(final String name, final Resource value) {
        return new HttpHeader(checkNotNullOrEmpty(checkNotNullOrEmpty(name, "Header name should not be null"), "Header name should not be null"),
                requireNonNull(requireNonNull(value, "Header value should not be null"), "Header value should not be null"));
    }

    public static RequestExtractor<String> cookie(final String key) {
        return new CookieRequestExtractor(checkNotNullOrEmpty(key, "Cookie key should not be null"));
    }

    public static ResponseHandler cookie(final String key, final String value, final CookieAttribute... attributes) {
        return cookie(checkNotNullOrEmpty(key, "Cookie key should not be null"),
                text(checkNotNullOrEmpty(value, "Cookie value should not be null")),
                requireNonNull(attributes, "Cookie options should not be null"));
    }

    public static ResponseHandler cookie(final String key, final Resource resource, final CookieAttribute... attributes) {
        return with(header(SET_COOKIE, cookieResource(
                checkNotNullOrEmpty(key, "Cookie key should not be null"),
                requireNonNull(resource, "Cookie value should not be null"),
                requireNonNull(attributes, "Cookie options should not be null"))));
    }

    public static RequestExtractor<String> form(final String key) {
        return new FormRequestExtractor(checkNotNullOrEmpty(key, "Form key should not be null"));
    }

    public static LatencyProcedure latency(final long duration, final TimeUnit unit) {
        checkArgument(duration > 0, "Latency must be greater than zero");
        return new LatencyProcedure(duration, requireNonNull(unit, "Time unit should not be null"));
    }

    public static RequestExtractor<String[]> query(final String param) {
        return new ParamRequestExtractor(checkNotNullOrEmpty(param, "Query parameter should not be null"));
    }

    public static XPathRequestExtractor xpath(final String xpath) {
        return new XPathRequestExtractor(checkNotNullOrEmpty(xpath, "XPath should not be null"));
    }

    public static ContentResource xml(final String resource) {
        return xml(text(checkNotNullOrEmpty(resource, "Resource should not be null")));
    }

    public static ContentResource xml(final Resource resource) {
        requireNonNull(resource, "Resource should not be null");
        return xmlResource((request) -> resource);
    }

    public static ContentResource json(final String jsonText) {
        return json(text(checkNotNullOrEmpty(jsonText, "Json should not be null")));
    }

    public static ContentResource json(final Resource resource) {
        requireNonNull(resource, "Json should not be null");
        return json((request) -> resource);
    }

    public static ContentResource json(final Function<Request, Object> function) {
        return jsonResource(requireNonNull(function, "Json function should not be null"));
    }

    public static ContentResource json(final Object pojo) {
        requireNonNull(pojo, "Json object should not be null");
        return json((request) -> pojo);
    }

    public static JsonPathRequestExtractor jsonPath(final String jsonPath) {
        return new JsonPathRequestExtractor(checkNotNullOrEmpty(jsonPath, "JsonPath should not be null"));
    }

    public static ResponseHandler seq(final String content, final String... contents) {
        requireNonNull(content, "Sequence content should not be null");
        checkArgument(contents.length > 0, "Sequence content should not be null");
        return newSeq(asIterable(content, contents).stream().map(textToResource()).toList());
    }

    public static ResponseHandler seq(final Resource content, final Resource... contents) {
        requireNonNull(content, "Sequence content should not be null");
        checkArgument(contents.length > 0, "Sequence contents should not be null");
        return newSeq(asIterable(content, contents).stream().map(Moco::with).toList());
    }

    public static ResponseHandler seq(final ResponseHandler handler, final ResponseHandler... handlers) {
        requireNonNull(handler, "Sequence handler should not be null");
        checkArgument(handlers.length > 0, "Sequence handlers should not be null");
        return newSeq(asIterable(handler, handlers));
    }

    public static ResponseHandler cycle(final String content, final String... contents) {
        requireNonNull(content, "Cycle content should not be null");
        checkArgument(contents.length > 0, "Cycle content should not be null");
        return newCycle(asIterable(content, contents).stream().map(textToResource()).toList());
    }

    public static ResponseHandler cycle(final Resource content, final Resource... contents) {
        requireNonNull(content, "Cycle content should not be null");
        checkArgument(contents.length > 0, "Cycle contents should not be null");
        return newCycle(asIterable(content, contents).stream().map(Moco::with).toList());
    }

    public static ResponseHandler cycle(final ResponseHandler handler, final ResponseHandler... handlers) {
        requireNonNull(handler, "Cycle handler should not be null");
        checkArgument(handlers.length > 0, "Cycle handlers should not be null");
        return newCycle(asIterable(handler, handlers));
    }

    public static ContentResource file(final String filename) {
        return file(text(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource file(final Resource filename) {
        return ApiUtils.file(requireNonNull(filename, "Filename should not be null"), null);
    }

    public static ContentResource file(final String filename, final Charset charset) {
        return ApiUtils.file(text(checkNotNullOrEmpty(filename, "Filename should not be null")), requireNonNull(charset, "Charset should not be null"));
    }

    public static ContentResource file(final Resource filename, final Charset charset) {
        return ApiUtils.file(requireNonNull(filename, "Filename should not be null"), requireNonNull(charset, "Charset should not be null"));
    }

    public static ContentResource pathResource(final String filename) {
        return pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource pathResource(final Resource filename) {
        return ApiUtils.pathResource(requireNonNull(filename, "Filename should not be null"), null);
    }

    public static ContentResource pathResource(final String filename, final Charset charset) {
        return ApiUtils.pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")), requireNonNull(charset, "Charset should not be null"));
    }

    public static ContentResource pathResource(final Resource filename, final Charset charset) {
        return ApiUtils.pathResource(requireNonNull(filename, "Filename should not be null"), requireNonNull(charset, "Charset should not be null"));
    }

    public static Resource version(final String version) {
        return version(HttpProtocolVersion.versionOf(checkNotNullOrEmpty(version, "Version should not be null")));
    }

    public static Resource version(final Resource resource) {
        return versionResource(requireNonNull(resource, "Version should not be null"));
    }

    public static Resource version(final HttpProtocolVersion version) {
        return versionResource(requireNonNull(version, "Version should not be null"));
    }

    public static ResponseHandler status(final int code) {
        checkArgument(code > 0, "Status code must be greater than zero");
        return new StatusCodeResponseHandler(code);
    }

    public static ResponseHandler proxy(final String url) {
        return proxy(checkNotNullOrEmpty(url, "URL should not be null"), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final ContentResource url) {
        return proxy(requireNonNull(url, "URL should not be null"), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final String url, final Failover failover) {
        return proxy(text(checkNotNullOrEmpty(url, "URL should not be null")),
                requireNonNull(failover, "Failover should not be null"));
    }

    public static ResponseHandler proxy(final ContentResource url, final Failover failover) {
        return new ProxyResponseHandler(toUrlFunction(requireNonNull(url, "URL should not be null")),
                requireNonNull(failover, "Failover should not be null"));
    }

    public static ResponseHandler proxy(final ProxyConfig proxyConfig) {
        return proxy(requireNonNull(proxyConfig), Failover.DEFAULT_FAILOVER);
    }

    public static ResponseHandler proxy(final ProxyConfig proxyConfig, final Failover failover) {
        return new ProxyBatchResponseHandler(requireNonNull(proxyConfig), requireNonNull(failover));
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
        return template(requireNonNull(resource, "Template should not be null"), Map.of());
    }

    public static ContentResource template(final ContentResource template, final String name, final String value) {
        return template(requireNonNull(template, "Template should not be null"),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value, "Template variable value should not be null")));
    }

    public static ContentResource template(final ContentResource template, final String name1, final String value1, final String name2, final String value2) {
        return template(requireNonNull(template, "Template should not be null"),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value1, "Template variable value should not be null")),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value2, "Template variable value should not be null")));
    }

    public static <T> ContentResource template(final String template, final String name, final RequestExtractor<T> extractor) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                requireNonNull(extractor, "Template variable extractor should not be null"));
    }

    public static <ExtractorType1, ExtractorType2> ContentResource template(final String template, final String name1, final RequestExtractor<ExtractorType1> extractor1,
                                                                            final String name2, final RequestExtractor<ExtractorType2> extractor2) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                requireNonNull(extractor1, "Template variable extractor should not be null"),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                requireNonNull(extractor2, "Template variable extractor should not be null"));
    }

    public static <T> ContentResource template(final ContentResource template, final String name, final RequestExtractor<T> extractor) {
        return templateResource(requireNonNull(template, "Template should not be null"),
                Map.of(checkValidVariableName(name),
                        new ExtractorVariable<>(requireNonNull(extractor, "Template variable extractor should not be null")))
        );
    }

    public static <ExtractorType1, ExtractorType2> ContentResource template(final ContentResource template, final String name1, final RequestExtractor<ExtractorType1> extractor1,
                                                                            final String name2, final RequestExtractor<ExtractorType2> extractor2) {
        return templateResource(requireNonNull(template, "Template should not be null"),
                Map.of(checkValidVariableName(name1),
                        new ExtractorVariable<>(requireNonNull(extractor1, "Template variable extractor should not be null")),
                        checkValidVariableName(name2),
                        new ExtractorVariable<>(requireNonNull(extractor2, "Template variable extractor should not be null")))
        );
    }

    public static ContentResource template(final String template,
                                           final Map<String, ? extends RequestExtractor<?>> variables) {
        return template(text(requireNonNull(template, "Template should not be null")),
                requireNonNull(variables, "Template variable should not be null"));
    }

    public static ContentResource template(final ContentResource template,
                                           final Map<String, ? extends RequestExtractor<?>> variables) {
        return templateResource(requireNonNull(template, "Template should not be null"),
                ApiUtils.toVariables(requireNonNull(variables, "Template variable should not be null")));
    }

    public static RequestExtractor<Object> var(final Function<Request, Object> supplier) {
        return new FunctionExtractor<>(requireNonNull(supplier, "Template variable should not be null or empty"));
    }

    public static RequestExtractor<Object> var(final Object obj) {
        requireNonNull(obj, "Template variable should not be null or empty");
        return var((request) -> obj);
    }

    public static Failover failover(final String file, final int... statuses) {
        return new Failover(ApiUtils.failoverExecutor(
                checkNotNullOrEmpty(file, "Failover filename should not be null")), FailoverStrategy.FAILOVER,
                requireNonNull(statuses, "Proxy status should not be null"));
    }

    public static Failover playback(final String file, final int... statuses) {
        return new Failover(ApiUtils.failoverExecutor(
                checkNotNullOrEmpty(file, "Playback filename should not be null")), FailoverStrategy.PLAYBACK,
                requireNonNull(statuses, "Proxy status should not be null"));
    }

    public static MocoEventTrigger complete(final MocoEventAction action) {
        return new MocoEventTrigger(MocoEvent.COMPLETE, requireNonNull(action, "Action should not be null"));
    }

    private static final int DEFAULT_LATENCY = 1000;

    public static MocoEventAction async(final MocoEventAction action) {
        return async(requireNonNull(action, "Action should not be null"),
                latency(DEFAULT_LATENCY, TimeUnit.MILLISECONDS));
    }

    public static MocoEventAction async(final MocoEventAction action, final LatencyProcedure procedure) {
        return new MocoAsyncAction(requireNonNull(action, "Action should not be null"),
                requireNonNull(procedure, "Procedure should not be null"));
    }

    public static MocoEventAction get(final String url, final HttpHeader... headers) {
        return get(text(checkNotNullOrEmpty(url, "URL should not be null")),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction get(final Resource url, final HttpHeader... headers) {
        return new MocoGetRequestAction(requireNonNull(url, "URL should not be null"),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final Resource url, final ContentResource content, final HttpHeader... headers) {
        return new MocoPostRequestAction(requireNonNull(url, "URL should not be null"),
                requireNonNull(content, "Content should not be null"),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final String url, final ContentResource content, final HttpHeader... headers) {
        return post(text(checkNotNullOrEmpty(url, "URL should not be null")),
                requireNonNull(content, "Content should not be null"),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final String url, final String content, final HttpHeader... headers) {
        return post(checkNotNullOrEmpty(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final Resource url, final String content, final HttpHeader... headers) {
        return post(requireNonNull(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static MocoEventAction post(final Resource url, final Object object, final HttpHeader... headers) {
        return post(requireNonNull(url, "URL should not be null"),
                Jsons.toJson(requireNonNull(object, "Content should not be null")),
                requireNonNull(headers, "Headers should not be null"));
    }

    public static ResponseHandler attachment(final String filename, final Resource resource) {
        return AndResponseHandler.and(
                with(header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(checkNotNullOrEmpty(filename, "Filename should not be null or empty")))),
                with(requireNonNull(resource, "Resource should not be null")));
    }

    public static ResponseHandler join(final MocoGroup group) {
        return new JoinResponseHandler(requireNonNull(group, "group should not be empty"));
    }

    public static MocoGroup group(final String name) {
        return new MocoGroup(checkNotNullOrEmpty(name, "group should not be empty"));
    }

    public static RequestMatcher conditional(final Predicate<Request> predicate) {
        return new ConditionalRequestMatcher(requireNonNull(predicate, "Predicate should not be null"));
    }

    private Moco() {
    }
}
