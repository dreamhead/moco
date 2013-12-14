package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;

public interface ResourceReader {
    byte[] readFor(HttpRequest request);
}
