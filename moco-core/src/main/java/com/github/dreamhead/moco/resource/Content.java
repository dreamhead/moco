package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;

public interface Content {
    String getContentType(final HttpRequest request);
}
