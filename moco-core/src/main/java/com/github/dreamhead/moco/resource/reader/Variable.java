package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;

public interface Variable {
    Object toTemplateVariable(final Request request);
}
