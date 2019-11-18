package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;

import java.util.Arrays;

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
    public final T response(final ResponseElement element, final ResponseElement... elements) {
        return this.response(with(checkNotNull(element, "Response element should not be null")),
                Arrays.stream(checkNotNull(elements, "Response elements should not be null"))
                        .map(Moco::with)
                        .toArray(ResponseHandler[]::new));
    }

    @Override
    public final T response(final Resource resource) {
        return this.response(with(checkNotNull(resource, "Resource should not be null")));
    }
}
