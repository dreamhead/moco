package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.monitor.CompositeMonitor;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.ExtractorVariable;
import com.github.dreamhead.moco.resource.reader.Variable;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformEntries;

public class ApiUtils {
    public static MocoMonitor mergeMonitor(final MocoMonitor monitor, final MocoMonitor monitor2, final MocoMonitor[] monitors) {
        MocoMonitor[] targetMonitors = new MocoMonitor[2 + monitors.length];
        targetMonitors[0] = checkNotNull(monitor, "Monitor should not be null");
        targetMonitors[1] = checkNotNull(monitor2, "Monitor should not be null");
        if (monitors.length > 0) {
            System.arraycopy(monitors, 0, targetMonitors, 2, monitors.length);
        }

        return new CompositeMonitor(targetMonitors);
    }

    public static Maps.EntryTransformer<String, RequestExtractor<?>, Variable> toVariable() {
        return new Maps.EntryTransformer<String, RequestExtractor<?>, Variable>() {
            @Override
            @SuppressWarnings("unchecked")
            public Variable transformEntry(final String key, final RequestExtractor<?> value) {
                return new ExtractorVariable(value);
            }
        };
    }

    public static ImmutableMap<String, Variable> toVariables(final ImmutableMap<String, ? extends RequestExtractor<?>> variables) {
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

    private ApiUtils() {
    }
}
