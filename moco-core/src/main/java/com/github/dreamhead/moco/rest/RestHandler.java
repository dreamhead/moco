package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
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
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class RestHandler extends AbstractHttpResponseHandler {
    private final String name;
    private final RestSetting[] settings;
    private final ResponseHandler notFoundHandler;
    private final ResponseHandler badRequestHandler;
    private final FluentIterable<GetAllRestSetting> getAllSettings;
    private final FluentIterable<GetSingleRestSetting> getSingleSettings;
    private final FluentIterable<PostRestSetting> postSettings;
    private final FluentIterable<PutRestSetting> putSettings;
    private final FluentIterable<DeleteRestSetting> deleteSettings;
    private final FluentIterable<HeadRestSetting> headSettings;

    public RestHandler(final String name, final RestSetting... settings) {
        this.name = name;
        this.settings = settings;
        this.notFoundHandler = status(HttpResponseStatus.NOT_FOUND.code());
        this.badRequestHandler = status(HttpResponseStatus.BAD_REQUEST.code());
        this.getAllSettings = toInstances(settings, GetAllRestSetting.class);
        this.getSingleSettings = toInstances(settings, GetSingleRestSetting.class);
        this.postSettings = toInstances(settings, PostRestSetting.class);
        this.putSettings = toInstances(settings, PutRestSetting.class);
        this.deleteSettings = toInstances(settings, DeleteRestSetting.class);
        this.headSettings = toInstances(settings, HeadRestSetting.class);
    }

    private <T extends RestSetting> FluentIterable<T> toInstances(final RestSetting[] settings, final Class<T> type) {
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
            return getSingleResponseHandler(this.headSettings, httpRequest);
        }

        return absent();
    }

    private Optional<ResponseHandler> getSingleResponseHandler(
            final FluentIterable<? extends RestSingleSetting> settings,
            final HttpRequest httpRequest) {
        Optional<? extends RestSingleSetting> setting = settings.firstMatch(matchSingle(httpRequest));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
        }

        return of(notFoundHandler);
    }

    private Function<RestSetting, ResponseHandler> toResponseHandler() {
        return new Function<RestSetting, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final RestSetting input) {
                return input.getHandler();
            }
        };
    }

    private Optional<ResponseHandler> getPostHandler(final HttpRequest request) {
        Optional<PostRestSetting> setting = postSettings.firstMatch(matchAll(request));
        if (setting.isPresent()) {
            return setting.transform(toResponseHandler());
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
        Optional<GetSingleRestSetting> matchedSetting = getSingleSettings.firstMatch(matchSingle(httpRequest));
        if (matchedSetting.isPresent()) {
            return matchedSetting.transform(toResponseHandler());
        }

        Optional<GetAllRestSetting> allRestSetting = getAllSettings.firstMatch(matchAll(httpRequest));
        if (allRestSetting.isPresent()) {
            return allRestSetting.transform(toResponseHandler());
        }

        if (by(uri(resourceRoot(name))).match(httpRequest)) {
            if (!getSingleSettings.isEmpty() && getSingleSettings.allMatch(isJsonHandlers())) {
                ImmutableList<Object> objects = getSingleSettings
                        .transform(toJsonHandler())
                        .transform(toPojo()).toList();
                return of(Moco.toJson(objects));
            }
        }

        return of(notFoundHandler);
    }

    private Predicate<RestAllSetting> matchAll(final HttpRequest request) {
        return new Predicate<RestAllSetting>() {
            @Override
            public boolean apply(final RestAllSetting input) {
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

    private Predicate<RestSingleSetting> matchSingle(final HttpRequest request) {
        return new Predicate<RestSingleSetting>() {
            @Override
            public boolean apply(final RestSingleSetting setting) {
                return setting.getRequestMatcher(name).match(request);
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

    private Function<RestSetting, JsonResponseHandler> toJsonHandler() {
        return new Function<RestSetting, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final RestSetting setting) {
                return JsonResponseHandler.class.cast(setting.getHandler());
            }
        };
    }

    private Predicate<RestSetting> isJsonHandlers() {
        return new Predicate<RestSetting>() {
            @Override
            public boolean apply(final RestSetting setting) {
                return setting.getHandler() instanceof JsonResponseHandler;
            }
        };
    }
}
