package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.resource.Resource;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractResponseBase<T> implements ResponseBase<T> {
    @Override
    public final T response(final String content) {
        return this.response(text(checkNotNullOrEmpty(content, "Content should not be null")));
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
    public final T response(final HttpHeader header) {
        return this.response(with(checkNotNull(header, "Http header should not be null")));
    }
}
