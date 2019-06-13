package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

public abstract class AbstractResponseBase<T> implements ResponseBase<T> {
    @Override
    public final T response(final String content) {
        return this.response(text(checkNotNullOrEmpty(content, "Content should not be null")));
    }

    @Override
    public final T response(final ResponseElement element, final ResponseElement... elements) {
        return this.response(with(checkNotNull(element, "Response element should not be null")),
                from(checkNotNull(elements, "Response elements should not be null"))
                        .transform(elementAsResponseHandler())
                        .toArray(ResponseHandler.class));
    }

    @Override
    public final T response(final Resource resource) {
        return this.response(with(checkNotNull(resource, "Resource should not be null")));
    }

    @Override
    public final T response(final MocoProcedure procedure) {
        return this.response(with(checkNotNull(procedure, "Procedure should not be null")));
    }

    @Override
    public final T response(final HttpHeader header, final HttpHeader... headers) {
        return this.response(with(checkNotNull(header, "Http header should not be null")),
                from(checkNotNull(headers, "Http headers should not be null"))
                        .transform(headerAsResponseHandler())
                        .toArray(ResponseHandler.class));
    }

    private Function<HttpHeader, ResponseHandler> headerAsResponseHandler() {
        return new Function<HttpHeader, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final HttpHeader input) {
                return with(input);
            }
        };
    }

    private Function<ResponseElement, ResponseHandler> elementAsResponseHandler() {
        return new Function<ResponseElement, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final ResponseElement input) {
                return with(input);
            }
        };
    }
}
