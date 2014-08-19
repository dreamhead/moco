package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Function;
import com.google.common.base.Optional;

public class FromRequestResourceReader implements ContentResourceReader {
    private final Function<Request, String> contentFunction;


    public FromRequestResourceReader(Function<Request, String> contentFunction) {
        this.contentFunction = contentFunction;
    }

    @Override
    public byte[] readFor(Optional<? extends Request> request) {
        if (!request.isPresent()) {
            throw new IllegalArgumentException("Request can not be absent.");
        }

        return contentFunction.apply(request.get()).getBytes();
    }

    public String getContentType() {
        return FileContentType.DEFAULT_CONTENT_TYPE;
    }

}
