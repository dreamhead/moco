package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public interface LogFormatter {
    String format(final Request request);
    String format(final Response response);
    String format(final Throwable e);
}
