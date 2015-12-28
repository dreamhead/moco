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
    private final String name;
    private final RequestMatcher allMatcher;
    private final RequestMatcher singleMatcher;
    private final ResponseHandler notFoundHandler;
    private final ResponseHandler badRequestHandler;
    private final FluentIterable<GetAllRestSetting> getAllSettings;
    private final FluentIterable<GetSingleRestSetting> getSingleSettings;
    private final FluentIterable<PostRestSetting> postSettings;
    private final FluentIterable<PutRestSetting> putSettings;
    private final FluentIterable<DeleteRestSetting> deleteSettings;
    private final FluentIterable<HeadSingleRestSetting> headSettings;
    private final FluentIterable<HeadAllRestSetting> headAllSettings;

    public RestRequestDispatcher(final String name, final RestSetting[] settings) {
        this.name = name;
        this.notFoundHandler = status(HttpResponseStatus.NOT_FOUND.code());
        this.badRequestHandler = status(HttpResponseStatus.BAD_REQUEST.code());
        this.getAllSettings = filter(settings, GetAllRestSetting.class);
        this.getSingleSettings = filter(settings, GetSingleRestSetting.class);
        this.postSettings = filter(settings, PostRestSetting.class);
        this.putSettings = filter(settings, PutRestSetting.class);
        this.deleteSettings = filter(settings, DeleteRestSetting.class);
        this.headSettings = filter(settings, HeadSingleRestSetting.class);
        this.headAllSettings = filter(settings, HeadAllRestSetting.class);
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

        return of(notFoundHandler);
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

        return of(notFoundHandler);
    }

    private Optional<ResponseHandler> getPostHandler(final HttpRequest request) {
        Optional<PostRestSetting> setting = postSettings.firstMatch(match(name, request));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        if (singleMatcher.match(request)) {
            return of(notFoundHandler);
        }

        return of(badRequestHandler);
    }

    private Optional<ResponseHandler> getSingleResponseHandler(
            final FluentIterable<? extends RestSingleSetting> settings,
            final HttpRequest httpRequest) {
        Optional<? extends RestSingleSetting> setting = settings.firstMatch(match(name, httpRequest));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        return of(notFoundHandler);
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
