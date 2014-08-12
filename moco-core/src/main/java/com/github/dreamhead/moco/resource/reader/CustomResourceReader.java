package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.google.common.base.Function;
import com.google.common.base.Optional;

public class CustomResourceReader implements ContentResourceReader {
    private final String contentType;
    private final Function<Request, String> contentFunction;


    public CustomResourceReader(String contentType, Function<Request, String> contentFunction) {
        this.contentType = contentType;
        this.contentFunction = contentFunction;
    }

    @Override
    public byte[] readFor(Optional<? extends Request> request) {
      return contentFunction.apply(request.get()).getBytes();
    }

    public String getContentType() {
        return this.contentType;
    }

}
