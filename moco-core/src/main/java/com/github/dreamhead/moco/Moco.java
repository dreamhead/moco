package com.github.dreamhead.moco;

import com.github.dreamhead.moco.action.MocoAsyncAction;
import com.github.dreamhead.moco.action.MocoRequestAction;
import com.github.dreamhead.moco.config.MocoContextConfig;
import com.github.dreamhead.moco.config.MocoFileRootConfig;
import com.github.dreamhead.moco.config.MocoRequestConfig;
import com.github.dreamhead.moco.config.MocoResponseConfig;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.extractor.CookieRequestExtractor;
import com.github.dreamhead.moco.extractor.FormRequestExtractor;
import com.github.dreamhead.moco.extractor.HeaderRequestExtractor;
import com.github.dreamhead.moco.extractor.JsonPathRequestExtractor;
import com.github.dreamhead.moco.extractor.ParamRequestExtractor;
import com.github.dreamhead.moco.extractor.PlainExtractor;
import com.github.dreamhead.moco.extractor.XPathRequestExtractor;
import com.github.dreamhead.moco.handler.AndResponseHandler;
import com.github.dreamhead.moco.handler.HeaderResponseHandler;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.handler.ProcedureResponseHandler;
import com.github.dreamhead.moco.handler.ProxyBatchResponseHandler;
import com.github.dreamhead.moco.handler.ProxyResponseHandler;
import com.github.dreamhead.moco.handler.SequenceHandler;
import com.github.dreamhead.moco.handler.StatusCodeResponseHandler;
import com.github.dreamhead.moco.handler.failover.DefaultFailoverExecutor;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.failover.FailoverStrategy;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ActualSocketServer;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.ContainMatcher;
import com.github.dreamhead.moco.matcher.EndsWithMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.ExistMatcher;
import com.github.dreamhead.moco.matcher.JsonRequestMatcher;
import com.github.dreamhead.moco.matcher.MatchMatcher;
import com.github.dreamhead.moco.matcher.NotRequestMatcher;
import com.github.dreamhead.moco.matcher.OrRequestMatcher;
import com.github.dreamhead.moco.matcher.StartsWithMatcher;
import com.github.dreamhead.moco.matcher.XmlRequestMatcher;
import com.github.dreamhead.moco.monitor.CompositeMonitor;
import com.github.dreamhead.moco.monitor.DefaultLogFormatter;
import com.github.dreamhead.moco.monitor.FileLogWriter;
import com.github.dreamhead.moco.monitor.LogMonitor;
import com.github.dreamhead.moco.monitor.LogWriter;
import com.github.dreamhead.moco.monitor.StdLogWriter;
import com.github.dreamhead.moco.procedure.LatencyProcedure;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.ExtractorVariable;
import com.github.dreamhead.moco.resource.reader.Variable;
import com.github.dreamhead.moco.util.Jsons;
import com.github.dreamhead.moco.util.URLs;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.extractor.Extractors.extractor;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static com.github.dreamhead.moco.resource.ResourceFactory.classpathFileResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.cookieResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.fileResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.methodResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.templateResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.textResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.uriResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.versionResource;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.net.HttpHeaders.SET_COOKIE;
import static java.lang.String.format;

public final class Moco {
    public static HttpServer httpServer(final int port, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createQuietServer(of(port), configs);
    }

    public static HttpServer httpServer(final int port, final MocoMonitor monitor, final MocoConfig... configs) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(of(port),
                checkNotNull(monitor, "Monitor should not be null"), configs);
    }

    public static HttpServer httpServer(final int port, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualHttpServer.createHttpServerWithMonitor(of(port), mergeMonitor(monitor, monitor2, monitors));
    }

    private static MocoMonitor mergeMonitor(final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor[] monitors) {
        MocoMonitor[] targetMonitors = new MocoMonitor[2 + monitors.length];
        targetMonitors[0] = checkNotNull(monitor, "Monitor should not be null");
        targetMonitors[1] = checkNotNull(monitor2, "Monitor should not be null");
        if (monitors.length > 0) {
            System.arraycopy(monitors, 0, targetMonitors, 2, monitors.length);
        }

        return new CompositeMonitor(targetMonitors);
    }

    public static HttpServer httpServer(final MocoConfig... configs) {
        return ActualHttpServer.createQuietServer(Optional.<Integer>absent(), configs);
    }

    public static HttpServer httpServer(final MocoMonitor monitor, final MocoConfig... configs) {
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

    public static SocketServer socketServer() {
        return ActualSocketServer.createQuietServer(Optional.<Integer>absent());
    }

    public static SocketServer socketServer(final int port) {
        return ActualSocketServer.createQuietServer(of(port));
    }

    public static SocketServer socketServer(final int port, final MocoMonitor monitor) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualSocketServer.createServerWithMonitor(of(port),
                checkNotNull(monitor, "Monitor should not be null"));
    }

    public static SocketServer socketServer(final int port, final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor... monitors) {
        checkArgument(port > 0, "Port must be greater than zero");
        return ActualSocketServer.createServerWithMonitor(of(port), mergeMonitor(monitor, monitor2, monitors));
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

    public static MocoConfig fileRoot(final String fileRoot) {
        return new MocoFileRootConfig(checkNotNullOrEmpty(fileRoot, "File root should not be null"));
    }

    public static MocoMonitor log() {
        return log(new StdLogWriter());
    }

    public static MocoMonitor log(final String filename) {
        return log(fileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty"), Optional.<Charset>absent()));
    }

    public static MocoMonitor log(final String filename, final Charset charset) {
        return log(fileLogWriter(checkNotNullOrEmpty(filename, "Filename should not be null or empty"), of(checkNotNull(charset, "Charset should not be null"))));
    }

    private static LogWriter fileLogWriter(final String filename, final Optional<Charset> charset) {
        return new FileLogWriter(filename, charset);
    }

    private static MocoMonitor log(final LogWriter writer) {
        return new LogMonitor(new DefaultLogFormatter(), writer);
    }

    public static RequestMatcher by(final String content) {
        return by(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static RequestMatcher by(final Resource resource) {
        checkNotNull(resource, "Resource should not be null");
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

    public static <T> RequestMatcher startsWith(final RequestExtractor<T> extractor, final String expected) {
        return startsWith(checkNotNull(extractor, "Extractor should not be null"),
                text(checkNotNullOrEmpty(expected, "Expected resource should not be null")));
    }

    private static <T> RequestMatcher startsWith(final RequestExtractor<T> extractor, final Resource resource) {
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
        return new AndRequestMatcher(copyOf(checkNotNull(matchers, "Matcher should not be null")));
    }

    public static RequestMatcher or(final RequestMatcher... matchers) {
        return new OrRequestMatcher(copyOf(checkNotNull(matchers, "Matcher should not be null")));
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

    public static RequestExtractor<String> cookie(final String key) {
        return new CookieRequestExtractor(checkNotNullOrEmpty(key, "Cookie key should not be null"));
    }

    public static ResponseHandler cookie(final String key, final String value) {
        return cookie(checkNotNullOrEmpty(key, "Cookie key should not be null"), text(checkNotNullOrEmpty(value, "Cookie value should not be null")));
    }

    public static ResponseHandler cookie(final String key, final Resource resource) {
        return header(SET_COOKIE, cookieResource(
                checkNotNullOrEmpty(key, "Cookie key should not be null"),
                checkNotNull(resource, "Cookie value should not be null")));
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
        return new XmlRequestMatcher((ContentRequestExtractor) extractor(resource.id()), resource);
    }

    public static RequestMatcher json(final String jsonText) {
        return json(text(checkNotNullOrEmpty(jsonText, "Json should not be null")));
    }

    public static RequestMatcher json(final Object pojo) {
        return json(Jsons.toJson(checkNotNull(pojo, "Json object should not be null")));
    }

    public static RequestMatcher json(final Resource resource) {
        checkNotNull(resource, "Json should not be null");
        return new JsonRequestMatcher((ContentRequestExtractor) extractor(resource.id()), resource);
    }

    public static ResponseHandler toJson(final Object pojo) {
        return new JsonResponseHandler(checkNotNull(pojo, "Json object should not be null"));
    }

    public static JsonPathRequestExtractor jsonPath(final String jsonPath) {
        return new JsonPathRequestExtractor(checkNotNullOrEmpty(jsonPath, "JsonPath should not be null"));
    }

    public static ResponseHandler seq(final String... contents) {
        checkArgument(contents.length > 0, "Sequence contents should not be null");
        return seq(FluentIterable.from(copyOf(contents)).transform(textToResource()).toList());
    }

    public static ResponseHandler seq(final Resource... contents) {
        checkArgument(contents.length > 0, "Sequence contents should not be null");
        return seq(FluentIterable.from(copyOf(contents)).transform(resourceToResourceHandler()).toList());
    }

    public static ResponseHandler seq(final ResponseHandler... handlers) {
        checkArgument(handlers.length > 0, "Sequence contents should not be null");
        return seq(copyOf(handlers));
    }

    private static ResponseHandler seq(final ImmutableList<ResponseHandler> handlers) {
        checkArgument(handlers.size() > 0, "Sequence contents should not be null");
        return new SequenceHandler(handlers);
    }

    public static ContentResource file(final String filename) {
        return file(text(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource file(final Resource filename) {
        return file(checkNotNull(filename, "Filename should not be null"), Optional.<Charset>absent());
    }

    public static ContentResource file(final String filename, final Charset charset) {
        return file(text(checkNotNullOrEmpty(filename, "Filename should not be null")), of(checkNotNull(charset, "Charset should not be null")));
    }

    public static ContentResource file(final Resource filename, final Charset charset) {
        return file(checkNotNull(filename, "Filename should not be null"), of(checkNotNull(charset, "Charset should not be null")));
    }

    public static ContentResource file(final String filename, final Optional<Charset> charset) {
        return file(text(checkNotNullOrEmpty(filename, "Filename should not be null")), checkNotNull(charset, "Charset should not be null"));
    }

    public static ContentResource file(final Resource filename, final Optional<Charset> charset) {
        return fileResource(checkNotNull(filename, "Filename should not be null"), checkNotNull(charset, "Charset should not be null"), Optional.<MocoConfig>absent());
    }

    public static ContentResource pathResource(final String filename) {
        return pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")));
    }

    public static ContentResource pathResource(final Resource filename) {
        return pathResource(checkNotNull(filename, "Filename should not be null"), Optional.<Charset>absent());
    }

    public static ContentResource pathResource(final String filename, final Charset charset) {
        return pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")), of(checkNotNull(charset, "Charset should not be null")));
    }

    public static ContentResource pathResource(final Resource filename, final Charset charset) {
        return pathResource(checkNotNull(filename, "Filename should not be null"), of(checkNotNull(charset, "Charset should not be null")));
    }

    public static ContentResource pathResource(final String filename, final Optional<Charset> charset) {
        return pathResource(text(checkNotNullOrEmpty(filename, "Filename should not be null")), checkNotNull(charset, "Charset should not be null"));
    }

    public static ContentResource pathResource(final Resource filename, final Optional<Charset> charset) {
        return classpathFileResource(checkNotNull(filename, "Filename should not be null"), checkNotNull(charset, "Charset should not be null"));
    }

    public static Resource version(final Resource resource) {
        return versionResource(checkNotNull(resource, "Version should not be null"));
    }

    public static Resource version(final String version) {
        return versionResource(HttpProtocolVersion.versionOf(checkNotNullOrEmpty(version, "Version should not be null")));
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
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                checkNotNullOrEmpty(value, "Template variable value should not be null"));
    }

    public static Resource template(final String template, final String name1, final String value1, final String name2, final String value2) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                checkNotNullOrEmpty(value1, "Template variable value should not be null"),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                checkNotNullOrEmpty(value2, "Template variable value should not be null"));
    }

    public static Resource template(final ContentResource resource) {
        return template(checkNotNull(resource, "Template should not be null"), ImmutableMap.<String, RequestExtractor<?>>of());
    }

    public static Resource template(final ContentResource template, final String name, final String value) {
        return template(checkNotNull(template, "Template should not be null"),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value, "Template variable value should not be null")));
    }

    public static Resource template(final ContentResource template, final String name1, final String value1, final String name2, final String value2) {
        return template(checkNotNull(template, "Template should not be null"),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value1, "Template variable value should not be null")),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                var(checkNotNullOrEmpty(value2, "Template variable value should not be null")));
    }

    public static <T> Resource template(final String template, final String name, final RequestExtractor<T> extractor) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name, "Template variable name should not be null"),
                checkNotNull(extractor, "Template variable extractor should not be null"));
    }

    public static <ExtractorType1, ExtractorType2> Resource template(final String template, final String name1, final RequestExtractor<ExtractorType1> extractor1,
                                                                     final String name2, final RequestExtractor<ExtractorType2> extractor2) {
        return template(text(checkNotNullOrEmpty(template, "Template should not be null")),
                checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                checkNotNull(extractor1, "Template variable extractor should not be null"),
                checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                checkNotNull(extractor2, "Template variable extractor should not be null"));
    }

    public static <T> Resource template(final ContentResource template, final String name, final RequestExtractor<T> extractor) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                ImmutableMap.of(checkNotNullOrEmpty(name, "Template variable name should not be null"),
                        new ExtractorVariable<T>(checkNotNull(extractor, "Template variable extractor should not be null")))
        );
    }

    public static <ExtractorType1, ExtractorType2> Resource template(final ContentResource template, final String name1, final RequestExtractor<ExtractorType1> extractor1,
                                                                     final String name2, final RequestExtractor<ExtractorType2> extractor2) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                ImmutableMap.of(checkNotNullOrEmpty(name1, "Template variable name should not be null"),
                        new ExtractorVariable<ExtractorType1>(checkNotNull(extractor1, "Template variable extractor should not be null")),
                        checkNotNullOrEmpty(name2, "Template variable name should not be null"),
                        new ExtractorVariable<ExtractorType2>(checkNotNull(extractor2, "Template variable extractor should not be null")))
        );
    }

    public static Resource template(final String template, final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
        return template(text(checkNotNull(template, "Template should not be null")),
                checkNotNull(variables, "Template variable should not be null"));
    }

    public static Resource template(final ContentResource template, final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
        return templateResource(checkNotNull(template, "Template should not be null"),
                toVariables(checkNotNull(variables, "Template variable should not be null")));
    }

    public static RequestExtractor<Object> var(final Object text) {
        return new PlainExtractor(checkNotNull(text, "Template variable should not be null or empty"));
    }

    public static Failover failover(final String file) {
        return new Failover(failoverExecutor(checkNotNullOrEmpty(file, "Filename should not be null")), FailoverStrategy.FAILOVER);
    }

    private static DefaultFailoverExecutor failoverExecutor(final String file) {
        return new DefaultFailoverExecutor(new File(checkNotNullOrEmpty(file, "Filename should not be null")));
    }

    public static Failover playback(final String file) {
        return new Failover(failoverExecutor(checkNotNullOrEmpty(file, "Filename should not be null")), FailoverStrategy.PLAYBACK);
    }

    public static MocoEventTrigger complete(final MocoEventAction action) {
        return new MocoEventTrigger(MocoEvent.COMPLETE, checkNotNull(action, "Action should not be null"));
    }

    public static MocoEventAction async(final MocoEventAction action) {
        return async(checkNotNull(action, "Action should not be null"), latency(LatencyProcedure.DEFAULT_LATENCY, TimeUnit.MILLISECONDS));
    }

    public static MocoEventAction async(final MocoEventAction action, final LatencyProcedure procedure) {
        return new MocoAsyncAction(checkNotNull(action, "Action should not be null"), checkNotNull(procedure, "Procedure should not be null"));
    }

    public static MocoEventAction get(final String url) {
        return new MocoRequestAction(checkNotNullOrEmpty(url, "URL should not be null"), HttpMethod.GET, Optional.<ContentResource>absent());
    }

    public static MocoEventAction post(final String url, final ContentResource content) {
        return new MocoRequestAction(checkNotNullOrEmpty(url, "URL should not be null"), HttpMethod.POST, of(checkNotNull(content, "Content should not be null")));
    }

    public static MocoEventAction post(final String url, final String content) {
        return post(checkNotNullOrEmpty(url, "URL should not be null"), text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    public static ResponseHandler attachment(final String filename, final Resource resource) {
        return AndResponseHandler.and(
                header(HttpHeaders.CONTENT_DISPOSITION, format("attachment; filename=%s", checkNotNullOrEmpty(filename, "Filename should not be null or empty"))),
                with(checkNotNull(resource, "Resource should not be null")));
    }

    private static Function<String, ResponseHandler> textToResource() {
        return new Function<String, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final String content) {
                return with(text(content));
            }
        };
    }

    private static Function<Resource, ResponseHandler> resourceToResourceHandler() {
        return new Function<Resource, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final Resource content) {
                return with(content);
            }
        };
    }

    private static ImmutableMap<String, Variable> toVariables(final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
        return ImmutableMap.copyOf(transformEntries(variables, toVariable()));
    }

    private static Maps.EntryTransformer<String, RequestExtractor<?>, Variable> toVariable() {
        return new Maps.EntryTransformer<String, RequestExtractor<?>, Variable>() {
            @Override
            @SuppressWarnings("unchecked")
            public Variable transformEntry(final String key, final RequestExtractor<?> value) {
                return new ExtractorVariable(value);
            }
        };
    }

    private Moco() {
    }
}
