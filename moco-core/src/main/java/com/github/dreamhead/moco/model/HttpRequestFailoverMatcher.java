package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Strings;

import java.util.Map;

public class HttpRequestFailoverMatcher {
    private final HttpRequest failover;

    public HttpRequestFailoverMatcher(final HttpRequest failover) {
        this.failover = failover;
    }

    public boolean match(final HttpRequest target) {
        return doMatch(failover.getUri(), target.getUri())
                && doMatch(failover.getVersion(), target.getVersion())
                && doMatch(failover.getContent(), target.getContent())
                && doMatch(failover.getHeaders(), target.getHeaders())
                && doMatch(failover.getMethod(), target.getMethod())
                && doMatch(failover.getQueries(), target.getQueries());
    }

    protected boolean doMatch(final Map<String, String> thisField, final Map<String, String> thatField) {
        if (thisField == null || thisField.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, String> entry : thisField.entrySet()) {
            if (!doMatch(entry.getValue(), thatField.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }

    protected boolean doMatch(final String thisField, final String thatField) {
        return Strings.isNullOrEmpty(thisField) || thisField.equals(thatField);
    }

    protected boolean doMatch(final HttpProtocolVersion thisField, final HttpProtocolVersion thatField) {
        return thisField == null || thisField == thatField;
    }

    protected boolean doMatch(final MessageContent thisField, final MessageContent thatField) {
        return thisField == null || thisField.equals(thatField);
    }
}
