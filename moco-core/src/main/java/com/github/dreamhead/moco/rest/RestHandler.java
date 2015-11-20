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

public class RestHandler extends AbstractHttpResponseHandler {
    private final String name;
    private final RestSetting[] settings;
    private final ResponseHandler notFoundHandler;
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
        this.getAllSettings = FluentIterable.of(settings)
                .filter(GetAllRestSetting.class)
                .transform(toInstance(GetAllRestSetting.class));
        this.getSingleSettings = FluentIterable.of(settings)
                .filter(GetSingleRestSetting.class)
                .transform(toInstance(GetSingleRestSetting.class));
        this.postSettings = FluentIterable.of(settings)
                .filter(PostRestSetting.class)
                .transform(toInstance(PostRestSetting.class));
        this.putSettings = FluentIterable.of(settings)
                .filter(PutRestSetting.class)
                .transform(toInstance(PutRestSetting.class));
        this.deleteSettings = FluentIterable.of(settings)
                .filter(DeleteRestSetting.class)
                .transform(toInstance(DeleteRestSetting.class));
        this.headSettings = FluentIterable.of(settings)
                .filter(HeadRestSetting.class)
                .transform(toInstance(HeadRestSetting.class));
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        if ("get".equalsIgnoreCase(httpRequest.getMethod())) {
            getGetHandler(httpRequest).writeToResponse(new SessionContext(httpRequest, httpResponse));
            return;
        }

        if ("post".equalsIgnoreCase(httpRequest.getMethod())) {
            Optional<ResponseHandler> postHandler = getPostHandler();
            if (postHandler.isPresent()) {
                postHandler.get().writeToResponse(new SessionContext(httpRequest, httpResponse));
                return;
            }
        }

        if ("put".equalsIgnoreCase(httpRequest.getMethod())) {
            Optional<PutRestSetting> putSetting = putSettings.firstMatch(matchSingle(httpRequest));
            if (putSetting.isPresent()) {
                putSetting.get().getHandler().writeToResponse(new SessionContext(httpRequest, httpResponse));
                return;
            }
        }

        if ("delete".equalsIgnoreCase(httpRequest.getMethod())) {
            Optional<DeleteRestSetting> putSetting = deleteSettings.firstMatch(matchSingle(httpRequest));
            if (putSetting.isPresent()) {
                putSetting.get().getHandler().writeToResponse(new SessionContext(httpRequest, httpResponse));
                return;
            }
        }

        if ("head".equalsIgnoreCase(httpRequest.getMethod())) {
            Optional<HeadRestSetting> putSetting = headSettings.firstMatch(matchSingle(httpRequest));
            if (putSetting.isPresent()) {
                putSetting.get().getHandler().writeToResponse(new SessionContext(httpRequest, httpResponse));
                return;
            }
        }

        throw new UnsupportedOperationException("Unsupported REST request");
    }

    private Optional<ResponseHandler> getPostHandler() {
        Optional<PostRestSetting> first = postSettings.first();
        if (first.isPresent()) {
            return Optional.of(first.get().getHandler());
        }
        return Optional.absent();

    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.URI_ID)) {
            return new RestHandler((String) config.apply(name), settings);
        }

        return super.apply(config);
    }

    private ResponseHandler getGetHandler(final HttpRequest httpRequest) {
        Optional<GetSingleRestSetting> matchedSetting = getSingleSettings.firstMatch(matchSingle(httpRequest));
        if (matchedSetting.isPresent()) {
            return matchedSetting.get().getHandler();
        }

        if (by(uri(resourceRoot(name))).match(httpRequest)) {
            Optional<GetAllRestSetting> allRestSetting = getAllSettings.firstMatch(matchAll(httpRequest));
            if (allRestSetting.isPresent()) {
                return allRestSetting.get().getHandler();
            }

            if (!getSingleSettings.isEmpty() && getSingleSettings.allMatch(isJsonHandlers())) {
                ImmutableList<Object> objects = getSingleSettings
                        .transform(toJsonHandler())
                        .transform(toPojo()).toList();
                return Moco.toJson(objects);
            }
        }

        return notFoundHandler;
    }

    private Predicate<? super GetAllRestSetting> matchAll(final HttpRequest request) {
        return new Predicate<GetAllRestSetting>() {
            @Override
            public boolean apply(final GetAllRestSetting input) {
                return input.getMatcher().match(request);
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
