package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.handler.AbstractHttpResponseHandler;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
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

public class RestHandler extends AbstractHttpResponseHandler {
    private final String name;
    private final RestSetting[] settings;
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

    public RestHandler(final String name, final RestSetting... settings) {
        this.name = name;
        this.settings = settings;
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
        this.singleMatcher = Moco.match(uri(join(resourceRoot(name), ".*")));
    }

    private <T extends RestSetting> FluentIterable<T> filter(final RestSetting[] settings, final Class<T> type) {
        return FluentIterable.of(settings)
                .filter(type)
                .transform(toInstance(type));
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        Optional<ResponseHandler> responseHandler = getSingleResponseHandler(httpRequest);
        if (responseHandler.isPresent()) {
            responseHandler.get().writeToResponse(new SessionContext(httpRequest, httpResponse));
            return;
        }

        throw new UnsupportedOperationException("Unsupported REST request");
    }

    private Optional<ResponseHandler> getSingleResponseHandler(final HttpRequest httpRequest) {
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
            return getSingleResponseHandler(this.deleteSettings, httpRequest);
        }

        if ("head".equalsIgnoreCase(httpRequest.getMethod())) {
            return getHeadHandler(httpRequest);
        }

        return absent();
    }

    private Optional<ResponseHandler> getSingleResponseHandler(
            final FluentIterable<? extends RestSingleSetting> settings,
            final HttpRequest httpRequest) {
        Optional<? extends RestSingleSetting> setting = settings.firstMatch(match(httpRequest));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        return of(notFoundHandler);
    }

    private Function<SimpleRestSetting, ResponseHandler> toResponseHandler() {
        return new Function<SimpleRestSetting, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final SimpleRestSetting input) {
                return input.getHandler();
            }
        };
    }

    private Optional<ResponseHandler> getPostHandler(final HttpRequest request) {
        Optional<PostRestSetting> setting = postSettings.firstMatch(match(request));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        if (singleMatcher.match(request)) {
            return of(notFoundHandler);
        }

        return of(badRequestHandler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            return new RestHandler((String) config.apply(name), settings);
        }

        return super.apply(config);
    }

    private Optional<ResponseHandler> getGetHandler(final HttpRequest httpRequest) {
        Optional<ResponseHandler> matchedSetting = getSingleOrAllHandler(httpRequest,
                this.getSingleSettings,
                this.getAllSettings);
        if (matchedSetting.isPresent()) {
            return matchedSetting;
        }

        if (allMatcher.match(httpRequest)) {
            if (!this.getSingleSettings.isEmpty() && this.getSingleSettings.allMatch(isJsonHandlers())) {
                ImmutableList<Object> objects = this.getSingleSettings
                        .transform(toJsonHandler())
                        .transform(toPojo()).toList();
                return of(Moco.toJson(objects));
            }
        }

        return of(notFoundHandler);
    }

    private Optional<ResponseHandler> getHeadHandler(final HttpRequest httpRequest) {
        Optional<ResponseHandler> handler = getSingleOrAllHandler(httpRequest, this.headSettings, this.headAllSettings);
        if (handler.isPresent()) {
            return handler;
        }

        return of(notFoundHandler);
    }

    private Optional<ResponseHandler> getSingleOrAllHandler(final HttpRequest httpRequest,
                                                    final FluentIterable<? extends RestSingleSetting> singleSettings,
                                                    final FluentIterable<? extends RestAllSetting> allSettings) {
        Optional<? extends RestSingleSetting> matchedSetting = singleSettings.firstMatch(match(httpRequest));
        if (matchedSetting.isPresent()) {
            return matchedSetting.transform(toResponseHandler());
        }

        Optional<? extends RestAllSetting> allRestSetting = allSettings.firstMatch(match(httpRequest));
        if (allRestSetting.isPresent()) {
            return allRestSetting.transform(toResponseHandler());
        }

        return absent();
    }

    private Predicate<SimpleRestSetting> match(final HttpRequest request) {
        return new Predicate<SimpleRestSetting>() {
            @Override
            public boolean apply(final SimpleRestSetting input) {
                return input.getRequestMatcher(name).match(request);
            }
        };
    }

    private <T> Function<? super T, T> toInstance(final Class<T> clazz) {
        return new Function<T, T>() {
            @Override
            public T apply(final T input) {
                return clazz.cast(input);
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

    private Function<SimpleRestSetting, JsonResponseHandler> toJsonHandler() {
        return new Function<SimpleRestSetting, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final SimpleRestSetting setting) {
                return JsonResponseHandler.class.cast(setting.getHandler());
            }
        };
    }

    private Predicate<SimpleRestSetting> isJsonHandlers() {
        return new Predicate<SimpleRestSetting>() {
            @Override
            public boolean apply(final SimpleRestSetting setting) {
                return setting.getHandler() instanceof JsonResponseHandler;
            }
        };
    }
}
