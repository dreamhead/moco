package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.util.Iterables;
import com.google.common.base.MoreObjects;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.get;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class GetSetting extends BaseActionSetting {
    private TextContainer url;
    private Map<String, TextContainer> headers;

    public MocoEventAction createAction() {
        if (headers == null) {
            return get(url.asResource());
        }

        HttpHeader[] headers = asHeaders(this.headers.entrySet());

        return get(url.asResource(), Iterables.head(headers), Iterables.tail(headers));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("headers", this.headers)
                .toString();
    }
}
