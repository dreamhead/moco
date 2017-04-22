package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.DefaultFailoverExecutor;
import com.github.dreamhead.moco.handler.failover.FailoverExecutor;
import com.github.dreamhead.moco.matcher.ContainMatcher;
import com.github.dreamhead.moco.matcher.EndsWithMatcher;
import com.github.dreamhead.moco.matcher.MatchMatcher;
import com.github.dreamhead.moco.matcher.StartsWithMatcher;
import com.github.dreamhead.moco.monitor.CompositeMonitor;
import com.github.dreamhead.moco.monitor.DefaultLogFormatter;
import com.github.dreamhead.moco.monitor.FileLogWriter;
import com.github.dreamhead.moco.monitor.LogMonitor;
import com.github.dreamhead.moco.monitor.LogWriter;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.ExtractorVariable;
import com.github.dreamhead.moco.resource.reader.Variable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.File;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.util.Iterables.asIterable;
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

    public static LogWriter fileLogWriter(final String filename, final Optional<Charset> charset) {
        return new FileLogWriter(filename, charset);
    }

    public static MocoMonitor log(final LogWriter writer) {
        return new LogMonitor(new DefaultLogFormatter(), writer);
    }

    public static <T> RequestMatcher match(final RequestExtractor<T> extractor, final Resource expected) {
        return new MatchMatcher<T>(extractor, expected);
    }

    public static <T> RequestMatcher startsWith(final RequestExtractor<T> extractor, final Resource resource) {
        return new StartsWithMatcher<T>(extractor, resource);
    }

    public static <T> RequestMatcher endsWith(final RequestExtractor<T> extractor, final Resource resource) {
        return new EndsWithMatcher<T>(extractor, resource);
    }

    public static <T> RequestMatcher contain(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<T>(extractor, resource);
    }

    private ApiUtils() {
    }
}
