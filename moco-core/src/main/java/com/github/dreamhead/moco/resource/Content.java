package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.util.MediaType;

public interface Content {
    MediaType getContentType(HttpRequest request);
}
