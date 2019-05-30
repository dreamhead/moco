package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.handler.failover.DefaultFailoverExecutor;
import com.github.dreamhead.moco.handler.failover.FailoverExecutor;
import com.github.dreamhead.moco.matcher.ContainMatcher;
import com.github.dreamhead.moco.matcher.EndsWithMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.JsonRequestMatcher;
import com.github.dreamhead.moco.matcher.MatchMatcher;
import com.github.dreamhead.moco.matcher.StartsWithMatcher;
import com.github.dreamhead.moco.monitor.CompositeMonitor;
import com.github.dreamhead.moco.monitor.DefaultLogFormatter;
import com.github.dreamhead.moco.monitor.FileLogWriter;
import com.github.dreamhead.moco.monitor.LogMonitor;
import com.github.dreamhead.moco.monitor.LogWriter;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.ExtractorVariable;
import com.github.dreamhead.moco.resource.reader.Variable;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.File;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.resource.ResourceFactory.classpathFileResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.fileResource;
import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformEntries;

public final class ApiUtils {
    public static MocoMonitor mergeMonitor(final MocoMonitor monitor, final MocoMonitor monitor2,
                                           final MocoMonitor[] monitors) {
        return new CompositeMonitor(asIterable(monitor, monitor2, monitors));
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

    public static ImmutableMap<String, Variable> toVariables(
            final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
        return ImmutableMap.copyOf(transformEntries(variables, toVariable()));
    }

    public static Function<String, ResponseHandler> textToResource() {
        return new Function<String, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final String content) {
                return Moco.with(Moco.text(content));
            }
        };
    }

    public static Function<Resource, ResponseHandler> resourceToResourceHandler() {
        return new Function<Resource, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final Resource content) {
                return Moco.with(content);
            }
        };
    }

    public static FailoverExecutor failoverExecutor(final String file) {
        return new DefaultFailoverExecutor(new File(file));
    }

    public static LogWriter fileLogWriter(final String filename, final Charset charset) {
        return new FileLogWriter(filename, charset);
    }

    public static MocoMonitor log(final LogWriter writer) {
        return new LogMonitor(new DefaultLogFormatter(), writer);
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final Resource expected) {
        return new MatchMatcher<>(extractor, expected);
    }

    public static <T> RequestMatcher startsWith(final RequestExtractor<T> extractor, final Resource resource) {
        return new StartsWithMatcher<>(extractor, resource);
    }

    public static <T> RequestMatcher endsWith(final RequestExtractor<T> extractor, final Resource resource) {
        return new EndsWithMatcher<>(extractor, resource);
    }

    public static <T> RequestMatcher contain(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<>(extractor, resource);
    }

    public static <T> RequestMatcher by(final RequestExtractor<T> extractor, final Resource expected) {
        if ("json".equalsIgnoreCase(expected.id())) {
            return new JsonRequestMatcher(expected, ContentRequestExtractor.class.cast(extractor));
        }

        return new EqRequestMatcher<>(extractor, expected);
    }

    public static ContentResource file(final Resource filename, final Charset charset) {
        return fileResource(checkNotNull(filename, "Filename should not be null"), charset, null);
    }

    public static ContentResource pathResource(final Resource filename, final Charset charset) {
        return classpathFileResource(checkNotNull(filename, "Filename should not be null"), charset);
    }

    public static ImmutableMap<String, Resource> toHeaders(final Iterable<HttpHeader> headers) {
        ImmutableMap.Builder<String, Resource> builder = ImmutableMap.builder();
        for (HttpHeader header : headers) {
            builder.put(header.getName(), header.getValue());
        }

        return builder.build();
    }

    private ApiUtils() {
    }
}
