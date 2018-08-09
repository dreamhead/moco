package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public interface LogFormatter {
    String format(Request request);
    String format(Response response);
    String format(Throwable e);
}
