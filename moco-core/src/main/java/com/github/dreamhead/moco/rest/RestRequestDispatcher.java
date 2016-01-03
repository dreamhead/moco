package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.HttpResponseStatus;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class RestRequestDispatcher {
    private static final ResponseHandler NOT_FOUND_HANDLER = status(HttpResponseStatus.NOT_FOUND.code());
    private static final ResponseHandler BAD_REQUEST_HANDLER = status(HttpResponseStatus.BAD_REQUEST.code());

    private final String name;
    private final RequestMatcher allMatcher;
    private final RequestMatcher singleMatcher;
    private final FluentIterable<? extends RestAllSetting> getAllSettings;
    private final FluentIterable<? extends RestSingleSetting> getSingleSettings;
    private final FluentIterable<? extends RestAllSetting> postSettings;
    private final FluentIterable<? extends RestSingleSetting> putSettings;
    private final FluentIterable<? extends RestSingleSetting> deleteSettings;
    private final FluentIterable<? extends RestSingleSetting> headSettings;
    private final FluentIterable<? extends RestAllSetting> headAllSettings;

    public RestRequestDispatcher(final String name, final RestSetting[] settings) {
        this.name = name;

        this.getAllSettings = filter(settings, RestAllSetting.class, HttpMethod.GET);
        this.getSingleSettings = filter(settings, RestSingleSetting.class, HttpMethod.GET);
        this.postSettings = filter(settings, RestAllSetting.class, HttpMethod.POST);
        this.putSettings = filter(settings, RestSingleSetting.class, HttpMethod.PUT);
        this.deleteSettings = filter(settings, RestSingleSetting.class, HttpMethod.DELETE);
        this.headSettings = filter(settings, RestSingleSetting.class, HttpMethod.HEAD);
        this.headAllSettings = filter(settings, RestAllSetting.class, HttpMethod.HEAD);
        this.allMatcher = by(uri(resourceRoot(name)));
        this.singleMatcher = Moco.match(uri(join(resourceRoot(name), "[^/]*")));
    }

    private <T> Function<? super T, T> toInstance(final Class<T> clazz) {
        return new Function<T, T>() {
            @Override
            public T apply(final T input) {
                return clazz.cast(input);
            }
        };
    }

    private <T extends RestSetting> FluentIterable<T> filter(final RestSetting[] settings,
                                                             final Class<T> type,
                                                             final HttpMethod method) {
        return filter(settings, type).filter(new Predicate<T>() {
            @Override
            public boolean apply(final T input) {
                return input.isFor(method);
            }
        });
    }

    private <T extends RestSetting> FluentIterable<T> filter(final RestSetting[] settings, final Class<T> type) {
        return FluentIterable.of(settings)
                .filter(type)
                .transform(toInstance(type));
    }

    private Predicate<SimpleRestSetting> isJsonHandlers() {
        return new Predicate<SimpleRestSetting>() {
            @Override
            public boolean apply(final SimpleRestSetting setting) {
                return setting.getHandler() instanceof JsonResponseHandler;
            }
        };
    }

    private Function<SimpleRestSetting, JsonResponseHandler> toJsonHandler() {
        return new Function<SimpleRestSetting, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final SimpleRestSetting setting) {
                return JsonResponseHandler.class.cast(setting.getHandler());
            }
        };
    }

    private Function<JsonResponseHandler, Object> toPojo() {
        return new Function<JsonResponseHandler, Object>() {
            @Override
            public Object apply(final JsonResponseHandler handler) {
                return handler.getPojo();
            }
        };
    }

    public Predicate<SimpleRestSetting> match(final String name, final HttpRequest request) {
        return new Predicate<SimpleRestSetting>() {
            @Override
            public boolean apply(final SimpleRestSetting input) {
                return input.getRequestMatcher(name).match(request);
            }
        };
    }

    private Function<SimpleRestSetting, ResponseHandler> toResponseHandler() {
        return new Function<SimpleRestSetting, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final SimpleRestSetting input) {
                return input.getHandler();
            }
        };
    }

    private Optional<ResponseHandler> getSingleOrAllHandler(final HttpRequest httpRequest,
                                                            final FluentIterable<? extends RestSingleSetting> single,
                                                            final FluentIterable<? extends RestAllSetting> all,
                                                            final String name) {
        Optional<? extends RestSingleSetting> matchedSetting = single.firstMatch(match(name, httpRequest));
        if (matchedSetting.isPresent()) {
            return matchedSetting.transform(toResponseHandler());
        }

        Optional<? extends RestAllSetting> allRestSetting = all.firstMatch(match(name, httpRequest));
        if (allRestSetting.isPresent()) {
            return allRestSetting.transform(toResponseHandler());
        }

        return absent();
    }

    private Optional<ResponseHandler> getHeadHandler(final HttpRequest httpRequest) {
        Optional<ResponseHandler> handler = getSingleOrAllHandler(httpRequest, headSettings, headAllSettings, name);
        if (handler.isPresent()) {
            return handler;
        }

        return of(NOT_FOUND_HANDLER);
    }

    private Optional<ResponseHandler> getGetHandler(final HttpRequest httpRequest) {
        Optional<ResponseHandler> matchedSetting = getSingleOrAllHandler(httpRequest,
                getSingleSettings,
                getAllSettings, name);
        if (matchedSetting.isPresent()) {
            return matchedSetting;
        }

        if (allMatcher.match(httpRequest)) {
            if (!getSingleSettings.isEmpty() && getSingleSettings.allMatch(isJsonHandlers())) {
                ImmutableList<Object> objects = getSingleSettings
                        .transform(toJsonHandler())
                        .transform(toPojo()).toList();
                return of(Moco.toJson(objects));
            }
        }

        return of(NOT_FOUND_HANDLER);
    }

    private Optional<ResponseHandler> getPostHandler(final HttpRequest request) {
        Optional<? extends RestAllSetting> setting = postSettings.firstMatch(match(name, request));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        if (singleMatcher.match(request)) {
            return of(NOT_FOUND_HANDLER);
        }

        return of(BAD_REQUEST_HANDLER);
    }

    private Optional<ResponseHandler> getSingleResponseHandler(
            final FluentIterable<? extends RestSingleSetting> settings,
            final HttpRequest httpRequest) {
        Optional<? extends RestSingleSetting> setting = settings.firstMatch(match(name, httpRequest));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        return of(NOT_FOUND_HANDLER);
    }

    public Optional<ResponseHandler> getResponseHandler(final HttpRequest httpRequest) {
        if (allMatcher.match(httpRequest) || this.singleMatcher.match(httpRequest)) {
            return doGetResponseHandler(httpRequest);
        }

        return absent();
    }

    private Optional<ResponseHandler> doGetResponseHandler(final HttpRequest httpRequest) {
        if ("get".equalsIgnoreCase(httpRequest.getMethod())) {
            return getGetHandler(httpRequest);
        }

        if ("post".equalsIgnoreCase(httpRequest.getMethod())) {
            return getPostHandler(httpRequest);
        }

        if ("put".equalsIgnoreCase(httpRequest.getMethod())) {
            return getSingleResponseHandler(putSettings, httpRequest);
        }

        if ("delete".equalsIgnoreCase(httpRequest.getMethod())) {
            return getSingleResponseHandler(deleteSettings, httpRequest);
        }

        if ("head".equalsIgnoreCase(httpRequest.getMethod())) {
            return getHeadHandler(httpRequest);
        }

        return absent();
    }
}
