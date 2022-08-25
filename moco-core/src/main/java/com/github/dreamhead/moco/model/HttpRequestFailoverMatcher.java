package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public final class HttpRequestFailoverMatcher {
    private final HttpRequest source;

    public HttpRequestFailoverMatcher(final HttpRequest source) {
        this.source = source;
    }

    public boolean match(final HttpRequest target) {
        return doMatch(source.getUri(), target.getUri())
                && doMatch(source.getVersion(), target.getVersion())
                && doMatch(source.getContent(), target.getContent())
                && doMatchHeaders(source.getHeaders(), target.getHeaders())
                && doMatch(source.getMethod(), target.getMethod())
                && doMatch(source.getQueries(), target.getQueries());
    }

    private boolean doMatchHeaders(final Map<String, ?> thisField, final Map<String, ?> thatField) {
        if (thisField == null || thisField.isEmpty()) {
            return true;
        }

        Map<String, Object> thisEnhanced = new HashMap<>();
        for (String key : thisField.keySet()) {
            thisEnhanced.put(key.toLowerCase(), thisField.get(key));
        }

        Map<String, Object> thatEnhanced = new HashMap<>();
        for (String key : thatField.keySet()) {
            thatEnhanced.put(key.toLowerCase(), thatField.get(key));
        }

        return thisEnhanced.entrySet().stream()
                .noneMatch(entry -> notMatchHeaderMapValue(entry.getValue(), thatEnhanced.get(entry.getKey())));
    }

    private boolean notMatchHeaderMapValue(final Object thisValue, final Object thatValue) {
        if (thisValue instanceof String && thatValue instanceof String) {
            return !doMatch(((String) thisValue).toLowerCase(), ((String) thatValue).toLowerCase());
        }

        if (thisValue instanceof String[] && thatValue instanceof String[]) {
            String[] thisValues = (String[]) thisValue;
            String[] thatValues = (String[]) thatValue;
            return !doMatchHeader(thisValues, thatValues);
        }

        return false;
    }

    private boolean doMatchHeader(final String[] thisValues, final String[] thatValues) {
        if (thisValues.length != thatValues.length) {
            return false;
        }

        return IntStream.range(0, thatValues.length)
                .allMatch(index -> doMatch(thisValues[index].toLowerCase(), thatValues[index].toLowerCase()));
    }

    private boolean doMatch(final Map<String, ?> thisField, final Map<String, ?> thatField) {
        if (thisField == null || thisField.isEmpty()) {
            return true;
        }

        return thisField.entrySet().stream()
                .noneMatch(entry -> notMatchMapValue(entry.getValue(), thatField.get(entry.getKey())));
    }


    private boolean notMatchMapValue(final Object thisValue, final Object thatValue) {
        if (thisValue instanceof String && thatValue instanceof String) {
            if (!doMatch((String) thisValue, (String) thatValue)) {
                return true;
            }
        }

        if (thisValue instanceof String[] && thatValue instanceof String[]) {
            String[] thisValues = (String[]) thisValue;
            String[] thatValues = (String[]) thatValue;
            if (!doMatch(thisValues, thatValues)) {
                return true;
            }
        }

        return false;
    }

    private boolean doMatch(final String[] thisValues, final String[] thatValues) {
        if (thisValues.length != thatValues.length) {
            return false;
        }

        return IntStream.range(0, thatValues.length)
                .allMatch(index -> doMatch(thisValues[index], thatValues[index]));
    }

    protected boolean doMatch(final String thisField, final String thatField) {
        return Strings.isNullOrEmpty(thisField) || thisField.equals(thatField);
    }

    protected boolean doMatch(final HttpProtocolVersion thisField, final HttpProtocolVersion thatField) {
        return thisField == null || thisField == thatField;
    }

    protected boolean doMatch(final MessageContent thisField, final MessageContent thatField) {
        return thisField == null || thisField.equals(thatField)
                || (thatField != null && thisField.toString().equals(thatField.toString()));
    }

    protected boolean doMatch(final HttpMethod thisField, final HttpMethod thatField) {
        return thisField == null || thisField == thatField;
    }
}
