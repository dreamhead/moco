package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Strings;

import java.util.Map;

public class HttpRequestFailoverMatcher {
    private final HttpRequest failover;

    public HttpRequestFailoverMatcher(HttpRequest failover) {
        this.failover = failover;
    }

    public boolean match(HttpRequest target) {
        return doMatch(failover.getUri(), target.getUri())
                && doMatch(failover.getVersion(), target.getVersion())
                && doMatch(failover.getContent(), target.getContent())
                && doMatch(failover.getHeaders(), target.getHeaders())
                && doMatch(failover.getMethod(), target.getMethod())
                && doMatch(failover.getQueries(), target.getQueries());
    }

    protected boolean doMatch(Map<String, String> thisField, Map<String, String> thatField) {
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

    protected boolean doMatch(String thisField, String thatField) {
        return Strings.isNullOrEmpty(thisField) || thisField.equals(thatField);
    }

    protected boolean doMatch(HttpProtocolVersion thisField, HttpProtocolVersion thatField) {
        return thisField == null || thisField == thatField;
    }

    protected boolean doMatch(MessageContent thisField, MessageContent thatField) {
        return thisField == null || thisField.equals(thatField);
    }
}
