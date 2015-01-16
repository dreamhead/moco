package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.Optional;

public interface ResourceReader {
    MessageContent readFor(final Optional<? extends Request> request);
}
