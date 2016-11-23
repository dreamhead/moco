package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.CookieAttribute;
import com.github.dreamhead.moco.parser.deserializer.CookieContainerDeserializer;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@JsonDeserialize(using = CookieContainerDeserializer.class)
public class CookieContainer implements Container {
    private String value;
    private String path;
    private String domain;
    private LatencyContainer maxAge;
    private boolean secure;
    private boolean httpOnly;
    private String template;

    public static CookieContainer newContainer(final String text) {
        CookieContainer container = new CookieContainer();
        container.value = text;
        return container;
    }

    public static CookieContainer newContainer(final String text, final String path,
                                               final String domain, final LatencyContainer maxAge,
                                               final boolean secure, final boolean httpOnly,
                                               final String template) {
        CookieContainer container = new CookieContainer();
        container.value = text;
        container.path = path;
        container.domain = domain;
        container.maxAge = maxAge;
        container.secure = secure;
        container.httpOnly = httpOnly;
        container.template = template;
        return container;
    }

    public final String getValue() {
        return value;
    }

    public final boolean isForTemplate() {
        return this.template != null;
    }

    public final String getTemplate() {
        return template;
    }

    public final CookieAttribute[] getOptions() {
        List<CookieAttribute> options = newArrayList();
        if (this.path != null) {
            options.add(CookieAttribute.path(this.path));
        }

        if (this.domain != null) {
            options.add(CookieAttribute.domain(this.domain));
        }

        if (this.secure) {
            options.add(CookieAttribute.secure());
        }

        if (this.httpOnly) {
            options.add(CookieAttribute.httpOnly());
        }

        if (this.maxAge != null) {
            options.add(CookieAttribute.maxAge(this.maxAge.getLatency(), this.maxAge.getUnit()));
        }

        return options.toArray(new CookieAttribute[options.size()]);
    }
}
