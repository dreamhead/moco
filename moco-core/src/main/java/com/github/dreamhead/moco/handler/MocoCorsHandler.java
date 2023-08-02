package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.handler.cors.CorsConfig;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MocoCorsHandler extends AbstractHttpResponseHandler {
    private final CorsConfig[] configs;

    public MocoCorsHandler(final CorsConfig[] configs) {
        this.configs = configs;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        String requestOrigin = httpRequest.getHeader("Origin");
        if (Strings.isNullOrEmpty(requestOrigin)) {
            return;
        }

        if (isSimpleRequest(httpRequest)) {
            writeSimpleResponse(httpRequest, httpResponse, CorsConfig::isSimpleRequestConfig);
            return;
        }

        if (httpRequest.getMethod() == HttpMethod.OPTIONS) {
            writeOptionResponse(httpRequest, httpResponse);
            return;
        }

        writeNoSimpleResponse(httpRequest, httpResponse);
    }

    private void writeOptionResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        if (configs.length == 0) {
            httpResponse.addHeader("Access-Control-Allow-Origin", "*");
            httpResponse.addHeader("Access-Control-Allow-Methods", "*");
            httpResponse.addHeader("Access-Control-Allow-Headers", "*");
            return;
        }

        writeCorsResponse(httpRequest, httpResponse, CorsConfig::isNonSimpleRequestConfig, false);
    }

    private void writeNoSimpleResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        if (configs.length == 0) {
            httpResponse.addHeader("Access-Control-Allow-Origin", "*");
            httpResponse.addHeader("Access-Control-Allow-Methods", "*");
            httpResponse.addHeader("Access-Control-Allow-Headers", "*");
            return;
        }

        writeCorsResponse(httpRequest, httpResponse, CorsConfig::isNonSimpleRequestConfig, true);
    }

    private void writeSimpleResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse, final Predicate<CorsConfig> isSimpleRequestConfig) {
        if (configs.length == 0) {
            httpResponse.addHeader("Access-Control-Allow-Origin", "*");
            return;
        }

        writeCorsResponse(httpRequest, httpResponse, isSimpleRequestConfig, true);
    }

    private void writeCorsResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse,
                                   final Predicate<CorsConfig> requestPredicate, final boolean isQualified) {
        List<CorsConfig> filteredConfigs = Arrays.stream(configs)
                .filter(requestPredicate)
                .collect(Collectors.toList());

        if (isQualified && !filteredConfigs.stream().allMatch(config -> config.isQualified(httpRequest))) {
            return;
        }

        for (CorsConfig config : filteredConfigs) {
            config.configure(httpResponse);
        }
    }

    private static final ImmutableSet<HttpMethod> simpleRequestMethods
            = ImmutableSet.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.POST);

    private boolean isSimpleRequest(final HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        return simpleRequestMethods.contains(method);
    }
}
